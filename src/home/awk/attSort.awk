# this awk program sorts attributes of the total.csv file in order to have 
# use. day, time, date&time as the first four 

BEGIN{ 
FS = ","
OFS = ","
}
#Deal with title line 
{
#
o25 = $4
o24 = $5
o26 = $6
adex = index($0,o25)
bdex = length($0) - index($0,$7)
a = substr($0,1,adex)
b = substr($0,index($0,$7),bdex)

cap =  "," o24 ", " o25 ", " o26

$0 = a b cap
gsub(/'/,"",$0)
print $0
}