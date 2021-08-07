file = open("rt.txt", "r")

data = file.readlines()
v = 0
i = 0
for l in data:
    v = v + float(l)
    i = i + 1
print(v/i)