import cv2
import numpy as np
from math import cos, sin,pi, atan2

# 4-point target centered at the origin.
quad_3d = np.float32([[-1,-1,0], [1, -1, 0], [1, 1, 0], [-1, 1, 0]])

fx=1
fy=1
w=320 # we'll use a square image for simplicity


K = np.float64([[fx*w, 0, 0], # note image coordinates relative to center of frame (cx=cy=0)
                [0, fx*w, 0],
                [0.0,0.0,      1.0]])



dist_coef = np.zeros(4)

def rprint(a):
    print(np.round(a, 3))

def solve(tag, imagePoints):
    print(tag)
    print("imagepoints")
    rprint(imagePoints)

    ret, rvec, tvec = cv2.solvePnP(quad_3d, imagePoints , K, dist_coef)

    print("R:")
    #print(rvec)
    rprint(cv2.Rodrigues(rvec)[0])
    #print("tvec")
    print("T:")
    rprint(tvec)

    projectionMatrix = np.concatenate((cv2.Rodrigues(rvec)[0], tvec),axis=1)
    #print(projectionMatrix)

    #print("decomposed")
    (cam,rot,tr,rx,ry,rz,eu)=cv2.decomposeProjectionMatrix(projectionMatrix)
    #print('cam:')
    #rprint(cam)
    #print("T:")
    #rprint(tr)
    print("R-decomp:")
    rprint(rot)
    #print(ry)
    print('Euler')
    print('degs')
    rprint(eu)
    print('rads')
    rprint(eu*pi/180.0)

    print('heading')
    rprint(atan2(tvec[0],tvec[2])*180.0/pi)




# Case 1 image straight on.
cameraPoseT = np.float32([0,0,10])
cameraPoseR = np.float64([[1, 0, 0],[0,1,0],[0,0,1]])  # to keep it simple, no reflection in y.
(cameraPoseRVec,_) = cv2.Rodrigues(cameraPoseR)
(imagePoints,_)=cv2.projectPoints(quad_3d, cameraPoseRVec, cameraPoseT, K, dist_coef)
solve('**STRAIGHT**', imagePoints)


# translate target by -10 in X.
cameraPoseT = np.float32([-10,0,10])
(imagePoints,_)=cv2.projectPoints(quad_3d, cameraPoseRVec, cameraPoseT, K, dist_coef)
solve('**LEFT**', imagePoints)

# rotate target by 90 around y.  This is a degenerate case for the solver and it does badly
cameraPoseT = np.float32([0,0,10])
cameraPoseR = np.float64([[0, 0, 1],[0,1,0],[-1,0,0]])  # top keep it simple, no reflection in y.
(cameraPoseRVec,_) = cv2.Rodrigues(cameraPoseR)
(imagePoints,_)=cv2.projectPoints(quad_3d, cameraPoseRVec, cameraPoseT, K, dist_coef)
solve('**ROT 90Y**', imagePoints)  # y is down. rotates right side towards camera


# rotate 90 around z
cameraPoseT = np.float32([0,0,10])
cameraPoseR = np.float64([[0, 1, 0],[-1,0,0],[0,0,1]])  # to keep it simple, no reflection in y.
(cameraPoseRVec,_) = cv2.Rodrigues(cameraPoseR)
(imagePoints,_)=cv2.projectPoints(quad_3d, cameraPoseRVec, cameraPoseT, K, dist_coef)
solve('**ROT 90Z**', imagePoints)  # y is down. rotates right side towards camera



# rotate target by 45 around y.
cameraPoseT = np.float32([0,0,10])
cameraPoseR = np.float64([[pi/4, 0, pi/4],[0,1,0],[-pi/4,0,pi/4]])  # to keep it simple, no reflection in y.
(cameraPoseRVec,_) = cv2.Rodrigues(cameraPoseR)
(imagePoints,_)=cv2.projectPoints(quad_3d, cameraPoseRVec, cameraPoseT, K, dist_coef)
solve('**ROT 45Y**', imagePoints)  # y is down. rotates right side towards camera

# now translate target x by +10 and rotate 45.
cameraPoseT = np.float32([10,0,10])
cameraPoseR = np.float64([[pi/4, 0, pi/4],[0,1,0],[-pi/4,0,pi/4]])  # to keep it simple, no reflection in y.
(cameraPoseRVec,_) = cv2.Rodrigues(cameraPoseR)
(imagePoints,_)=cv2.projectPoints(quad_3d, cameraPoseRVec, cameraPoseT, K, dist_coef)
solve('**T+10 ROT 45Y**', imagePoints)  # y is down. rotates right side towards camera

# now translate target x by +5 and rotate 45.
cameraPoseT = np.float32([5,0,10])
cameraPoseR = np.float64([[pi/4, 0, pi/4],[0,1,0],[-pi/4,0,pi/4]])  # to keep it simple, no reflection in y.
(cameraPoseRVec,_) = cv2.Rodrigues(cameraPoseR)
(imagePoints,_)=cv2.projectPoints(quad_3d, cameraPoseRVec, cameraPoseT, K, dist_coef)
solve('**T+5 ROT 45Y**', imagePoints)  # y is down. rotates right side towards camera

# now translate target x by +20 and rotate 45.
cameraPoseT = np.float32([20,0,10])
cameraPoseR = np.float64([[pi/4, 0, pi/4],[0,1,0],[-pi/4,0,pi/4]])  # to keep it simple, no reflection in y.
(cameraPoseRVec,_) = cv2.Rodrigues(cameraPoseR)
(imagePoints,_)=cv2.projectPoints(quad_3d, cameraPoseRVec, cameraPoseT, K, dist_coef)
solve('**T+20 ROT 45Y**', imagePoints)  # y is down. rotates right side towards camera

# now translate target x by -5 and rotate 45.
cameraPoseT = np.float32([-5,0,10])
cameraPoseR = np.float64([[pi/4, 0, pi/4],[0,1,0],[-pi/4,0,pi/4]])  # to keep it simple, no reflection in y.
(cameraPoseRVec,_) = cv2.Rodrigues(cameraPoseR)
(imagePoints,_)=cv2.projectPoints(quad_3d, cameraPoseRVec, cameraPoseT, K, dist_coef)
solve('**T-5 ROT 45Y**', imagePoints)  # y is down. rotates right side towards camera

#verts = ar_verts * [(x1-x0), (y1-y0), -(x1-x0)*0.3] + (x0, y0, 0)
#verts = cv2.projectPoints(verts, rvec, tvec, K, dist_coef)[0].reshape(-1, 2)

# Straight on case but y-axis is inverted. We rotate around X by 180, so Z gets inverted as well.  
# This is necessary because OCV uses right-handed coordinate systems.
cameraPoseT = np.float32([0,0,10])
cameraPoseR = np.float64([[1, 0, 0],[0,-1,0],[0,0,-1]])  # Rotate 180 around X
(cameraPoseRVec,_) = cv2.Rodrigues(cameraPoseR)
(imagePoints,_)=cv2.projectPoints(quad_3d, cameraPoseRVec, cameraPoseT, K, dist_coef)
solve('**STRAIGHT-Inverted**', imagePoints)

