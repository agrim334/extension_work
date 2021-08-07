file = open("PC_RTs.txt", "r")

data = file.readlines()
v = 0
i = 0
for l in data:
    v = v + float(l)*1000
    i = i + 1
print(i)
print(v/i)