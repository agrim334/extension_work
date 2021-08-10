fname = input("Enter .txt file(full name with extension) containing the response times (numbers only)") 
file = open(fname, "r")

data = file.readlines()
v = 0
i = 0
for l in data:
    v = v + float(l)*1000
    i = i + 1
print(i)
print(v/i)