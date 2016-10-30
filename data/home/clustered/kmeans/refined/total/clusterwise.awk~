 # awk program to split dataset clusterwise

BEGIN{
FS = ","
OFS = ","

# define the number of clusters and name corresponding .csv file
num = 20

for(i=0;i<num;i++){
    cluster = "cluster" i
    file = i
    clusters[cluster] = file  ".csv"
} 

}

# print header
NR < 2{
    for( x in clusters ){
	print $0 > clusters[x]
    }
    
}

# match cluster and print body
NR > 1{
   print $0 > clusters[$24]
}
