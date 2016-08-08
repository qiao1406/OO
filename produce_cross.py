import random

fr = open('crossinfo1.txt','w')
cross = [ [0] * 80 for i in range(80)]

for i in range(80):
    for j in range(80):
        cross[i][j] = random.randint(0,1)


for i in range(80):
    for j in range(80):
        fr.write(str(cross[i][j]))
    fr.write('\n')
fr.close()
