# stepCounter
> ### Kyujeong & YoungBean's research project
### 1. 삼성헬스의 만보기 기능을 스텝카운터 센서를 쓰지 않고 구현
> <img src="http://blogfiles9.naver.net/MjAxNzA3MjlfMjUz/MDAxNTAxMzE1NTk0NzE0.V9MguvZMU3bmzZKzmEqbMjGsDClxqhyKI3mhd-itsEUg.cUeimuJq60XFNZMYs43oRR7wNUgciulOka5636Io8Yog.PNG.kkkclub1/Screenshot_20170729-160933.png" width="60%" height="30%">

### 2. 4가지의 경우를 구별하여 인식
1. *손에 들고 걷기*
2. *주머니에 넣고 걷기*
3. *문자하면서 걷기*
4. *전화하면서 걷기*

### 3. 가속센서와 자이로센서로 걸음수 측정

### 4. 4가지 경우에 따른 각각의 사용 센서
- *손에 들고 걷기* - **가속센서, 자이로센서**
- *주머니에 넣고 걷기* - **가속센서, 근접센서**
- *문자하면서 걷기* - **가속센서, 자이로센서**
- *전화하면서 걷기* - **가속센서, 근접센서**

### 5. 4가지 경우로 측정한 걸음수의 총합을 출력

### Graph
- X,Y,Z의 그래프
> ![xyz](https://user-images.githubusercontent.com/21302833/34510888-f7406930-f09a-11e7-990e-c11ce1ec9e4a.png)
- angleXZ, angleYZ의 그래프
> ![xzyz](https://user-images.githubusercontent.com/21302833/34510891-fa660cf0-f09a-11e7-880f-898b222cf4fb.png)
