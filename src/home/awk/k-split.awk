BEGIN{ 
FS = ","
OFS =","
}
NR==1{
#print title row
print $0 > "c1_cl.csv"
print $0 > "c2_cl.csv"
print $0 > "c3_cl.csv"
print $0 > "c4_cl.csv"
print $0 > "c5_cl.csv"
print $0 > "c6_cl.csv"
}
NR!=1{
    # week cutoff points
    w1 = 10082
    w2 = 20160
    w3 = 30240
    w4 = 40320
    w5 = 50400 
    w6 = 60480
    # print instances into corresponding weeks
    # week 2 has 2 less than all others
    if(NR < w1){
	print $0 > "c1_cl.csv"
    }else if( NR < w2 ){
	print $0 > "c2_cl.csv"
    }else if( NR < w3 ){
	print $0 > "c3_cl.csv"
    }else if( NR < w4 ) {
	print $0 > "c4_cl.csv"
    }else if( NR < w5 ) {
	print $0 > "c5_cl.csv"
    }else if ( NR < w6 ) {
	print $0 > "c6_cl.csv"
    }
}





