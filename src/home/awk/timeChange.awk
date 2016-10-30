# Change a time value into one of four phases MORNING,AFTERNOON,EVENING,NIGHT <0,1,2,3>
BEGIN{
FS = ","
OFS = ","
}

#Print header
NR<2{
print $0
}

#Categorize the 0 ---> 235900 time value into a 0-->3
NR>1{
    
    time = $2
    
if( time <65900 ){ 
#night
    phase = 3
} 
else if( time < 110000){  
#morning
    phase = 0
}
else if( time < 170000){
#afternoon
    phase = 1
}
else if( time < 233000 ){
#evening
    phase = 2
}
else{ 
#night
    phase = 3
}

$2 = phase;

print $0

}
