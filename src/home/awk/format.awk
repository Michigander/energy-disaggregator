# this is a formatter for .csv files. its purpose is to split a timestamp of type (2014-05-05 08:09:00) into 
# day and time attributes. DAY = [0,1] weekday/weekend. TIME = [000000 - 235900].

BEGIN{
#Set my input/output field separators 
FS = ","
OFS = ","
}

#Must first split title row field 1
NR<2{

print "Day, ""Time, "$0
}

#In non-title rows I will split field 1
NR>1{

    #Split the time field at " " into day and time
    x = index($1," ");

    #Format day
    rawDay = substr($1,1,x-1);  
    gsub(/-/,"",rawDay); #Remove "-"'s
    year = substr(rawDay,1,4) + 0;
    yearnum = substr(rawDay,3,2) + 0;
    century = substr(rawDay,1,2) + 0;
    month = substr(rawDay,5,2) + 0;
    day = substr(rawDay,7,2) + 0;

    #Calculate day of the week 
    m = ((month%12)+10)%12; #Zeller's month number
    if( month < 3 ) century = century - 1; #Adjust Century
    f = day + int((13*m-1)/5) + yearnum + int(yearnum/4) + int(century/4) - 2*century;  #Zeller's formula 
    dow = f%7;

    #Determine if weekday/weekend
    dow = (dow+1)%7;
    if(dow<2) 
	dow = 0; #weekend
    else 
	dow = 1; #weekday

    #Format time
    rawTime = substr($1,x+1,length($1) );
    gsub(/:/,"",rawTime);
    gsub(/'/,"",rawTime);

    #Print results 
    print dow" , "rawTime" , "$0
}

END{
}