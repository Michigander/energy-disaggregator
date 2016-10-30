# reduce a dataset to the grouping for set C
# {(0,11)(9,6)(1,8)(10,13)} // mapping for reducing clusters
BEGIN{
FS = "," 
OFS = ","
}
NR<2{
print $0
}
NR>1{
    cluster = $24
    if( cluster == "cluster0" ) $24 = "cluster11";
    if( cluster == "cluster9" ) $24 = "cluster6";
    if( cluster == "cluster1" )$24 = "cluster8";
    if( cluster == "cluster10" ) $24 = "cluster13";
    print $0
}
