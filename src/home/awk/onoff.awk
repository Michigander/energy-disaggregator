#This is a program to take an attribute's raw wattage and replace it with 
#an On/Off binary value
BEGIN{
#
    FS = ","
    OFS = ","

# Set circuit cutoffs for lights
    cutoff[9]  = .1;       # LR patio lights 
    cutoff[10] = .05;      # GB hall lights 
    cutoff[13] = .07;       # Cellar lights
    cutoff[17] = .175;     # Kitchen lights
    cutoff[19] = .075;     # Bedroom lights
    cutoff[21] = .15;      # Master lights
}
#Header remains the same
NR<2{print$0}

NR>1{
#Transform raw wattages into on or off
    for(x in cutoff){
	if( $x > cutoff[x]){
	    $x = 1;
	}else 
	    $x = 0;
    }
#Print all attributes
print $0

}