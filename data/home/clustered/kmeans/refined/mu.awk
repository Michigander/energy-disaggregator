# match the date and time of an instance in order to add proper time
BEGIN{
FS = ","
OFS = ","
}
# store first file
FNR == NR{
    use1[FNR] = $2;
}
# store second file
FNR != NR{
    if(FNR<2){ header = $0 }else
    total[FNR] = $0
}
# write with proper use
END{
    print "refinedUse , "header 
    for( x in use1 ){
	print use1[x]" , "total[x]
    }
}