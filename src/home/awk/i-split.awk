#improved version of split

BEGIN{ 
    # build array of filenames
    for( i=1 ; i<7 ; i++ ) files[i] = "kmeans" i ".csv"
}

NR<2{
    # print title row
 #   for( x in files ) print $0 > files[i] 
}

NR>1{
    #keep a count of all instances 
    count++					
    #keep an array of all instances
    instances[NR] = $0
}

END{
    start = 2
    end = start + int(count/6)
 

    for( x=1;x<7;x++) {
	print "file  " x  " begins at instance " start " and ends at  " end 
	for( y=start;y<end+1;y++ ){
	    #print instances[y] > files[x]
	}
	start = end+1 
	end = start + int(count/6)
    }

}





