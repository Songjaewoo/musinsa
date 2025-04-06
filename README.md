# 백엔드 과제

### DB 테이블
| PRODUCT    |
|------------|
| 카테고리 (PK)  |
| 브랜드 (PK)   | 
| 가격         |

## 접근 방식 및 구현
 1. 실제 테이블에 저장되는 데이터 방식이 위 테이블 구조와 비슷할거라 생각하여 DB 구조는 변경 없이 정렬 자료형을 사용하여 구현.
 2. 상품 추가, 변경, 삭제 시 Sorted 데이터를 가공.
 3. 운영 적용시에는 Java Sorted 대신 Redis Sorted Set으로 대체
 4. 어플 구동시 초기 데이터는 입력됩니다.


## Sorted 데이터 예시
```commandline
# 카테고리별 가격 정렬
상의=[10000/C, 10100/D, 10500/G, 10500/B, 10700/E, 10800/H, 11200/F, 11200/A, 11400/I] 
아우터=[5000/E, 5100/D, 5500/A, 5800/G, 5900/B, 6200/C, 6300/H, 6700/I, 7200/F]
바지=[3000/D, 3100/H, 3200/I, 3300/C, 3800/E, 3800/B, 3900/G, 4000/F, 4200/A]
스니커즈=[9000/G, 9000/A, 9100/B, 9200/C, 9300/F, 9500/I, 9500/D, 9700/H, 9900/E]
가방=[2000/A, 2100/H, 2100/F, 2100/B, 2200/G, 2200/C, 2300/E, 2400/I, 2500/D]
모자=[1500/D, 1600/H, 1600/F, 1700/I, 1700/G, 1700/A, 1800/E, 1900/C, 2000/B]
양말=[1700/I, 1800/A, 2000/H, 2000/B, 2100/G, 2100/E, 2200/C, 2300/F, 2400/D]
액세서리=[1900/F, 2000/H, 2000/G, 2000/D, 2100/E, 2100/C, 2200/B, 2300/A, 2400/I]
```
```commandline
# 총가격 정렬
[36100/D, 37100/C, 37200/G, 37600/H, 37600/B, 37700/E, 37700/A, 39000/I, 39600/F]
```

## API 명세
###`1. GET /minPrice` 카테고리별 최저가 조회

    curl 'localhost:8080/minPrice'

```json
{
    "code": 200,
    "data": {
        "최저가": [
            {
                "카테고리": "상의",
                "브랜드": "C",
                "가격": "10,000"
            }
            ...
            ...
        ],
        "총액": "34,100"
    }
}
```

###`2. GET /minPriceBrand` 브랜드 최저가 조회

    curl 'localhost:8080/minPriceBrand'

```json
{
  "code": 200,
  "data": {
    "최저가": [
      {
        "카테고리": "가방",
        "가격": "2,500"
      }
      ...
      ...
    ],
    "브랜드": "D",
    "총액": "36,100"
  }
}
```

###`3. GET /minMaxPrice` 카테고리 최저/최고가 조회

    curl 'localhost:8080/minMaxPrice?category=%EC%83%81%EC%9D%98'

```json
{
  "code": 200,
  "data": {
    "카테고리": "상의",
    "최고가": {
      "브랜드": "I",
      "가격": "11,400"
    },
    "최저가": {
      "브랜드": "C",
      "가격": "10,000"
    }
  }
}
```

###`4-1. POST /product` 브랜드 및 상품 추가

    curl 'localhost:8080/product' \
    --header 'Content-Type: application/json' \
    --data '{
        "brand": "Z",
        "items": [
            {"category": "상의" , "price": 1000},
            {"category": "아우터", "price": 1000},
            {"category": "바지", "price": 1000},
            {"category": "스니커즈", "price": 1000},
            {"category": "가방", "price": 1000},
            {"category": "모자", "price": 1000},
            {"category": "양말", "price": 1000},
            {"category": "액세서리", "price": 1000}
        ]
    }'

```json
{
  "code": 200,
  "message": "상품 등록 성공"
}
```
###`4-2. PUT /price` 상품 가격 수정

    curl --request PUT 'localhost:8080/price' \
    --header 'Content-Type: application/json' \
    --data '{
        "category": "상의",
        "brand": "A",
        "updatePrice": 20000
    }'

```json
{
  "code": 200,
  "message": "가격 변경 성공"
}
```

###`4-3. DELETE /brand/{brand}` 브랜드 삭제

    curl --request DELETE 'localhost:8080/brand/A'

```json
{
  "code": 200,
  "message": "브랜드 삭제 성공"
}
```
## 실행 방법
```shell
빌드
./gradlew build

테스트
./gradlew test

실행
cd build/libs
java -jar musinsa.jar
```