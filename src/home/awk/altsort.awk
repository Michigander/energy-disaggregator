BEGIN{
FS = ","
OFS = ","
}
{
    use = $7 
    $7 = $4 
    $4 = use 

    cap1 = $5
    cap3 = $6
    cap2 = $7
    
    for( i=8; i<NF+1; i++ ) $(i-3) = $i
    
    $28 = cap1
    $29 = cap2
    $30 = cap3

    print $0
}
