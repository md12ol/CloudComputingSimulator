### Constants # / ##
global numTasks;      # (N) Number of tasks
global alpha;         # Weight of transmission energy
global beta;          # Weight of processing cost
global rho;           # Weight of total delay
global fSubA;         # CPU Frequency at CAP
global x;             # (x_i) x value of task i (local or CAP) (list)
global y;             # (y_i) y value of task i (CAP or RC) (list)
global dataIn;        # (D_in(i)) Input data size for task i (list)
global dataOut;       # (D_out(i)) Output data size for task i (list)
global cyclesPerBit;  # (App(i)) App type

numTasks = 10;
dataIn =[]; # Set
dataOut =[]; # Set
cyclesPerBit = 1900.0 / 8.0;



alpha = 0.0; # Set
beta = 0.0; # Set
row = 0.0; # Set
fSubA = 0.0; # Set
x =[]; # Set
y =[]; # Set


### Functions ###
# Transmission energy and processing cost for task i being processed at CAP
function transEnergy = transEnergy(


  function eSubAi = eSubAi(i)
# Fill in
return
endfunction

# Transmission energy and processing cost for task i being processed at RC
function eSubCi(i)
# Fill in
return
endfunction

# Sum of processing delay at local user for all tasks
function timeL = timeL();
# Fill in
return
endfunction

# Sum of transmission and processing delay at CAP for all tasks
function timeAP = timeAP();
# Fill in
return
endfunction

# Sum of transmission and processing delat at RC for all tasks
function timeRC = timeRC();
# Fill in
return
endfunction



# Matrix G_p from paper
function gSubP = gSubP(p);
# Fill in
return
endfunction

# Matrix G_0 from paper
function gNot = gNot();
# Fill in
return
endfunction

# Matrix A_0 from paper
function aNot = aNot();
# Fill in
return
endfunction

# Matrix (A_0)'
function aNotPrime = aNotPrime();
# Fill in
return
endfunction

# Matrix A_a
function aSubA = aSubA();
# Fill in
return
endfunction

# Matrix (A_a)'
function aSubAPrime = aSubAPrime();
# Fill in
return
endfunction

# Matrix A_c
function aSubC = a SubC();
# Fill in
return
endfunction

# Matrix (A_c)'
function aSubCPrime = aSubCPrime();
# Fill in
return
endfunction

# Matrix b_0
function bNot = bNot();
# Fill in
return
endfunction

# Matrix b_l
function bSubL = bSubL();
# Fill in
return
endfunction

# Matrix b_a
function bSubA = bSubA();
# Fill in
return
endfunction

# Matrix (b_a)'
function bSubAPrime = bSubAPrime();
# Fill in
return
endfunction

# Matrix b_c
function bSubC = bSubC();
# Fill in
return
endfunction

# Matrix e_i
function eSubI = eSubI(x , i); # Done, working
eSubI = zeroPrime(x);
eSubI(cast(i, "int32")) = 1;
return
endfunction

# Matrix (e_i)'
function eSubIPrime = eSubIPrime();
# Fill in
return
endfunction

# Matrix
function zeroM = zero(x);
zeroM = zeros(x, x);
return
endfunction

function zeroPrime = zeroPrime(x); # Done, working
zeroPrime = zeros(x, 1);
return
endfunction

### Variables ###
d =[1, 2, 3, 4];
c =[1; -1; 0; 0;];

gnot =[];
gl =[];
ga =[];
gc =[];

### Procedure ###
zeroPrime(numTasks)
eSubI(10, 1)

### Notes ###
# Note zero - based.  Top - left corner of 2 by 2 matrix is (1, 1)