import csv 
with open('week1.test.csv','rb') as csvfile:
    reader = csv.reader(csvfile, delimiter=' ', quotechar = '') 
    for row in reader:
        str = ","
        print row 
        print str.join(row)
