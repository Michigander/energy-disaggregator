BEGIN{
FS = "," 
OFS = ","
}
NR<2{
print "timestamp , use "
}
NR>1{
    $2 = $2 - ($3+$13+$14+$16+$24);
    if( $2 < 0 ) $2 = 0; 
    print $1" , "$2;
}
