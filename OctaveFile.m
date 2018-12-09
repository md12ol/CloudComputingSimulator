### Constants ###
# These are all the constants from the paper

global numTasks;      # (N) Number of tasks
global constA;         # Weight of transmission energy
global constB;          # Weight of processing cost
global constR;           # Weight of total delay
global fSubL;         # CPU Frequency locally
global fSubA;         # CPU Frequency at CAP
global fSubC;         # CPU Frequency at RC
global x;             # (x_i) x value of task i (local or CAP) (list)
global y;             # (y_i) y value of task i (CAP or RC) (list)
global dataIn;        # (D_in(i)) Input data size for task i (list)
global dataOut;       # (D_out(i)) Output data size for task i (list)
global cyclesPerBit;  # (App(i)) App type
global localTrans;    # (C_UL or C_DL) Local --> CAP transmission rate
global remoteTrans;   # (R_ac) CAP --> RC transmission rate
global localTransER;  # Local transmission energy rate
global localProcER;   # Local processing energy rate
global maxInput;      # Max of dataIn
global minInput;      # Min of dataIn
global maxOutput;     # Max of dataOut
global minOutput;     # Min of dataOut

numTasks = 10;
cyclesPerBit = 1900.0 / 8.0;
localTrans = 72.2 * power(10,6);
remoteTrans = 15 * power(10,6);
constA = 2 * power(10, -7);
constB = 5 * power(10, -7);
constR = 1;
fSubL = 500 * power(10,6);
fSubA = 5 * power(10,9);
fSubC = 10 * power(10,9);
localTransER = 1.42 * power(10,-7);
localProcER = 1 / (730 * power(10,6));
maxInput = 30 * 8 * power(10,6);
minInput = 10 * 8 * power(10,6);
maxOutput = 3 * 8 * power(10,6);
minOutput = 1 * 8 * power(10,6);

### Scenario setup ###
# Rendomly assign input and output sizes for the tasks

dataIn = (rand(1,numTasks) .* (maxInput - minInput)) .+ minInput;
dataOut = (rand(1,numTasks) .* (maxOutput - minOutput)) .+ minOutput;
disp("Input Data Vector");
disp(dataIn');
disp("Output Data Vector");
disp(dataOut');

# If you want to hardcode a solution then use these vectors
x = []; # x vector from paper
y = []; # y vector from paper

### Procedure ###
# This is the code that implements SeDuMi

G0 = reshape(gNot(), 1, []);
Gl = reshape(gSubL(), 1, []);
Ga = reshape(gSubA(), 1, []);
Gc = reshape(gSubC(), 1, []);

A = [Gl', Ga', Gc'];
temp = [];
for i = 1:(numTasks * 2)
  Gp = reshape(gSubP(i), 1, []);
  temp = [temp, Gp'];
endfor
A = [A, temp];
temp = (-1) .* temp;
A = [A, temp];
A = A';
c = G0;
temp = zeros(1, numTasks * 4);
b = [(-tSubL()' * matrixOne()), 0, 0, temp]';
[xOut, yOut, infoOut] = sedumi(A, b, c);

### Transform SeDuMi Output ###
# This is the code to convert output to useful data
# All capped variables are from algorithm in the paper

N = numTasks;
L = 100;
X = reshape(xOut, [columns(gNot()),rows(gNot())]);
X_PRIME = X(1:(2*N), 1:(2*N));
printToFile(X_PRIME, "X_PRIME Matrix.txt");

u = []';

### Helper Functions ###
# These functions are for output and for implementing algorithm from paper

# Output matrix to file
function printToFile(matrix, fileName)
  outFile = fopen(fileName, "w");
  outFormat = rptStr("%f\t", rows(matrix));
  fprintf(outFile,outFormat, matrix);
  fclose(outFile);
endfunction

# Repeats the string str the number of times provided, appends newline at end
function rtn = rptStr(str, times)
  rtn = "";
  for i = 1:times
    rtn = [rtn str];
  endfor
  rtn = [rtn "\n"];
  return
endfunction

### Paper Functions ###
# These functions are used by the above code to implement everything from the paper.

# Transmission energy to tranmit task with given size and transmission energy rate
function transEnergy = transEnergy(size, eRate) # Tested
  transEnergy = eRate * size;
  return
endfunction

# Transmission time to transmit a task with given size and transmission rate
function transTime = transTime(size, tRate) # Tested
  transTime = size / tRate;
  return
endfunction

# Processing time to process a task with given size and processing rate
function procTime = procTime(size, pRate) # Tested
  global cyclesPerBit;
  procTime = size * cyclesPerBit / pRate;
  return
endfunction

# Processing cost for task i being processed locally
function eSubLi = eSubLi(i); # Tested
  global dataIn;
  global localProcER;
  global cyclesPerBit;
  global localProcER;
  eSubLi = dataIn(i) * cyclesPerBit * localProcER;
  return
endfunction

# Transmission energy and processing cost for task i being processed at CAP
function eSubAi = eSubAi(i); # Tested
  global constA;
  global dataIn;
  global localTrans;
  global localTransER;
  eSubAi = 2 * transEnergy(dataIn(i), localTransER) + constA * dataIn(i);
  return
endfunction

# Transmission energy and processing cost for task i being processed at RC
function eSubCi = eSubCi(i); # Tested
  global constB;
  global dataIn;
  global localTrans;
  eSubCi = 2 * transEnergy(dataIn(i), localTrans) + constB * dataIn(i);
  return
endfunction

# Total time to process task i locally
function timeL = timeL(i); # Tested
  global fSubL;
  global x;
  global dataIn;
  timeL = procTime(dataIn(i), fSubL); # Process
  return
endfunction

# Sum of processing delay at local user for all tasks
function timeLSum = timeLSum(); # Tested
  global numTasks;
  global x;
  timeLSum = 0;
  for i = 1:numTasks
    if (x(i) == 0)
      timeLSum = timeLSum + timeL(i);
    endif
  endfor
  return
endfunction

# Transmission and processing delay at CAP for task i
function timeAP = timeAP(i); # Tested
  global fSubA;
  global x;
  global y;
  global dataIn;
  global dataOut;
  global localTrans;
  timeAP = 0;
  if (x(i) == 1)
    timeAP = transTime(dataIn(i), localTrans); # Up
    if (y(i) == 0)
      timeAP = timeAP + procTime(dataIn(i), fSubA); # Process
    endif
    timeAP = timeAP + transTime(dataOut(i), localTrans); # Down
  endif
  return
endfunction

# Sum of transmission and processing delay at RC for all tasks
function timeAPSum = timeAPSum(); # Tested
  global numTasks;
  timeAPSum = 0;
  for i = 1:numTasks
    timeAPSum = timeAPSum + timeAP(i);
  endfor
  return
endfunction

# Transmission and processing delay at RC for task i
function timeRC = timeRC(i) # Tested
  global fSubC;
  global x;
  global y;
  global dataIn;
  global dataOut;
  global localTrans;
  global remoteTrans;
  timeRC = 0;
  if (x(i) == 1 && y(i) == 1)
    timeRC = timeRC + transTime(dataIn(i), localTrans); # To CAP
    timeRC = timeRC + transTime(dataIn(i), remoteTrans); # To RC
    timeRC = timeRC + procTime(dataIn(i), fSubC); # Process
    timeRC = timeRC + transTime(dataOut(i), remoteTrans); # To CAP
    timeRC = timeRC + transTime(dataOut(i), localTrans); # To Local
  endif
  return
endfunction

# Sum of transmission and processing delat at RC for all tasks
function timeRCSum = timeRCSum(); # Tested
  global numTasks;
  global fSubC;
  global x;
  global y;
  global dataIn;
  global localTrans;
  global remoteTrans;
  timeRCSum = 0;
  for i = 1:numTasks
    timeRCSum = timeRCSum + timeRC(i);
  endfor
  return
endfunction

# Variable t from paper, the max of the three sums
function maxTime = maxTime() # Tested
  local = timeLSum();
  cap = timeAPSum();
  rc = timeRCSum();
  if (local > cap)
    maxTime = local;
  else
    maxTime = cap;
  endif
  if rc > maxTime
    maxTime = rc;
  endif
  return
endfunction

# Matrix w from paper
function w = w() # Tested
  global x;
  global y;
  global numTasks;
  w = [x, y, maxTime()]';
  return
endfunction

# Matrix E_l from paper
function eSubL = eSubL() # Tested
  global numTasks;
  eSubL = eSubLi(1:numTasks)';
  return
endfunction

# Matrix E_t from paper
function eSubT = eSubT() # Tested
  global numTasks;
  global dataIn;
  global localTransER;
  eSubT = arrayfun(@(x) transEnergy(x,localTransER), dataIn)';
  return
endfunction

# Matrix E_r from paper
function eSubR = eSubR() # Tested
  global numTasks;
  global dataOut;
  global localTransER;
  eSubR = arrayfun(@(x) transEnergy(x, localTransER), dataOut)';
  return
endfunction

# Matrix C_k from paper
function cSubK = cSubK() # Tested
  global dataIn;
  cSubK = dataIn;
  cSubK = cSubK';
  return
endfunction

# Matrix D_in from paper
function dSubIn = dSubIn() # Tested
  global dataIn;
  dSubIn = dataIn';
  return
endfunction

# Matrix D_out from paper
function dSubOut = dSubOut() # Tested
  global dataOut;
  dSubOut = dataOut';
  return
endfunction

# Matrix App from paper
function App = App() # Tested
  global numTasks;
  global cyclesPerBit;
  App = repelem(cyclesPerBit, numTasks);
  return
endfunction

# Matrix e_i from paper
function eSubI = eSubI(i); # Tested
  global numTasks;
  eSubI = zeroPrime(numTasks);
  eSubI(i) = 1;
  return
endfunction

# Matrix (e_i)' from paper
function rtn = eSubIPrime(i); # Tested
  global numTasks;
  rtn = zeroPrime(numTasks * 2 + 1);
  rtn(i) = 1;
  return
endfunction

# Matrix 0 from paper
function zero = zero(); # Tested
  global numTasks;
  zero = zeros(numTasks, numTasks);
  return
endfunction

# Matrix 0' from paper
function zeroPrime = zeroPrime(n); # Tested
  zeroPrime = zeros(n,1);
  return
endfunction

# Matrix z from paper
function matrixZ = matrixZ(); # Tested
  global numTasks;
  matrixZ = [w()',1];
  return
endfunction

# Matrix T_l from paper
function tSubL = tSubL() # Tested
  global numTasks;
  tSubL = timeL(1:numTasks)';
  return
endfunction

# Matrix 1 from paper
function matrixOne = matrixOne() # Tested
  global numTasks;
  matrixOne = ones(numTasks, 1);
  return
endfunction

# Matrix (A_0)'
function aNotPrime = aNotPrime(); # Tested
  global constA;
  global constB;
  aNotPrime = (1/2) .* diag(-constA .* cSubK() + constB .* cSubK());
  return
endfunction

# Matrix A_0 from paper
function aNot = aNot(); # Tested
  global numTasks;
  aNot = [zero(), aNotPrime(), zeroPrime(numTasks);
          aNotPrime()', zero(), zeroPrime(numTasks);
          zeroPrime(numTasks)', zeroPrime(numTasks)', 0];
  return
endfunction

# Matrix (A_a)'
function aSubAPrime = aSubAPrime(); # Tested
  global fSubA;
  aSubAPrime = (-1/(2 * fSubA)) * diag(dSubIn()) * diag(App());
  return
endfunction

# Matrix A_a
function aSubA = aSubA(); # Tested
  global numTasks;
  aSubA = [zero(), aSubAPrime(), zeroPrime(numTasks);
            aSubAPrime()', zero(), zeroPrime(numTasks);
            zeroPrime(numTasks)', zeroPrime(numTasks)', 0];
  return
endfunction

# Matrix (A_c)'
function aSubCPrime = aSubCPrime(); # Tested
  global remoteTrans;
  global fSubC;
  aSubCPrime = (1/2) * ((1/remoteTrans) .* (diag(dSubIn() + dSubOut()))
                + (1/fSubC) .* (diag(dSubIn()) * diag(dSubOut())));
  return
endfunction

# Matrix A_c
function aSubC = aSubC(); # Tested
  global numTasks;
  aSubC = [zero(), aSubCPrime(), zeroPrime(numTasks)();
            aSubCPrime(), zero(), zeroPrime(numTasks);
            zeroPrime(numTasks)', zeroPrime(numTasks)', 0];
  return
endfunction

# Matrix b_0
function bNot = bNot(); # Tested
  global constA;
  global constR;
  global numTasks;
  bNot = [(-eSubL()+eSubT+eSubR()+(constA .* cSubK()))', zeroPrime(numTasks)', constR]';
  return
endfunction

# Matrix b_l
function bSubL = bSubL(); # Tested
  global numTasks;
  bSubL = (-1) .* [tSubL()', zeroPrime(numTasks)', 1]';
  return
endfunction

# Matrix (b_a)'
function bSubAPrime = bSubAPrime(); # Tested
  global localTrans;
  global fSubA;
  bSubAPrime = (1/localTrans) .* dSubIn() + (1/localTrans) .* dSubOut() + (1/fSubA) .* (diag(dSubIn()) *
  App());
  return
endfunction

# Matrix b_a
function bSubA = bSubA(); # Tested
  global numTasks;
  bSubA = [bSubAPrime()', zeroPrime(numTasks)', -1]';
  return
endfunction

# Matrix b_c
function bSubC = bSubC(); # Tested
  global localTrans;
  global numTasks;
  bSubC = [((1/localTrans) .* dSubIn() + (1/localTrans) * dSubOut())', zeroPrime(numTasks)', -1]';
  return
endfunction

# Matrix G_0 from paper
function gNot = gNot(); # Tested
  global numTasks;
  gNot = [aNot(), (1/2) .* bNot();
           (1/2) .* bNot()', 0];
  return
endfunction

# Matrix G_l from paper
function gSubL = gSubL() # Tested
  global numTasks;
  gSubL = [zeros(numTasks * 2 + 1, numTasks * 2 + 1), (1/2) .* bSubL();
            (1/2) .* bSubL()', 0];
  return
endfunction

# Matrix G_a
function gSubA = gSubA() # Tested
  gSubA = [aSubA(), (1/2) .* bSubA();
            (1/2) .* bSubA()', 0];
  return
endfunction

# Matrix G_c
function gSubC = gSubC() # Tested
  gSubC = [aSubC(), (1/2) .* bSubC();
            (1/2) .* bSubC()', 0];
  return
endfunction

# Matrix G_p from paper
function gSubP = gSubP(p) # Tested
  gSubP = [diag(eSubIPrime(p)), (-1/2) .* eSubIPrime(p);
            (-1/2) .* eSubIPrime(p)', 0];
  return
endfunction

# Matrix X from the paper
function matrixX = matrixX() # Tested
  matrixX = matrixZ() * matrixZ()';
  return
endfunction