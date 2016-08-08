from random import choice


def randomRoad(x, y):
    if x == 79 and y == 79:
        return 0
    if x == 79:
        return choice([0,1])
    if y == 79:
        return choice([0,2])
    return choice([0,1,2,3])

def alloc(cityMap, index):
    """
       assert any point in {(x, y) | x > i, y > i} is connected.

    """
    # special case
    if index == 79:
        cityMap[79][79] = randomRoad(79, 79)
        return

    
    for i in range(79,index,-1):
        while( cityMap[index][i] == 0 ):
            cityMap[index][i] = randomRoad(index, i)
    for i in range(79,index-1,-1):
        while( cityMap[i][index] == 0 ):
            cityMap[i][index] = randomRoad(i, index)
    
def output(cityMap):
    fr = open('map.txt','w')
	
    for i in range(80):
        for j in range(80):
            fr.write(str(cityMap[i][j]))
            fr.write(' ')
        fr.write('\n')

    fr.close()





cityMap = [ [0] * 80 for i in range(80)]
for i in range(79, -1, -1):
    alloc(cityMap, i)

output(cityMap)
    
