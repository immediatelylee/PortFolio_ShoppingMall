# project_shoppingmall

혼자서 진행한 쇼핑몰 프로젝트입니다.

프리미엄 영유아 쇼핑몰입니다.

# Views

* 전체 프로젝트 설명 (마우스 우클릭 - 새탭에서 열기)
<a href="https://www.youtube.com/watch?v=dvROwZE9FsQ" target="_blank">
    <img src="https://img.youtube.com/vi/dvROwZE9FsQ/0.jpg" alt="포트폴리오 설명 영상">
</a>

[전체 프로젝트설명 영상 바로가기](https://www.youtube.com/watch?v=dvROwZE9FsQ)

* 프로젝트의 각 기능을 설명한 부분을 모아서 연결한 영상입니다.
* 영상 아래는 각 기능을 나눠서 설명한 부분이며 이 영상을 한번 보는것이 더 프로젝트를 이해하기 좋습니다.
* 썸네일 클릭시에 해당페이지에서 유튜브로 이동되므로 마우스 우클릭후 새탭에서 열어주시면 감사하겠습니다.

  
<br />
<br />
<br />
<br />
<br />

# Description

* 참여 인원: 1명(Front/Backend)
* 사용 기술
  * Spring boot,Spring Data JPA,QueryDsl,Spring Security
  * IntelliJ , Java 11, Thymeleaf, thumbnailator,jquery
  * Mysql ,H2,iamport 

&nbsp;
* 메인페이지  

  
![1-Clipchamp-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/2978fcd5-aa4c-4f78-8052-6f29857879e2)

  * 부트스트랩을 삭제하고 새로 뷰를 작성
  * bxSlider를 이용해여 이미지 슬라이더 구성
  * 이미지 슬라이더의 메뉴를 기존 슬라이더에서 좀더 쇼핑몰에 맞게 커스텀하였습니다.
  * javscript를 이용하여 상품에 관련한 이벤트나 탭에의한 이동을 구현하였음.


&nbsp;

* 로그인

![2-Clipchamp-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/245384d2-1ea6-4f6d-ba55-eb9d09baf981)

  * 부트스트랩 로그인 페이지를 사용하지 않고 직접 만들었습니다.
  * 실제 쇼핑몰의 로그인 페이지를 참조
  * 아이디,비밀번호 공백시에 알림이 있도록 스크립트 작성

&nbsp;
* 상품관리 페이지
  
![3번영상상품관리페이지소개-Clipchamp로-제작](https://github.com/user-attachments/assets/95caac63-e7f6-4e9f-9c52-923dd1454c94)

  * 상품에 관한 CRUD가 가능한 구조로 만들었습니다.
  * 여기서도 부트스트랩을 사용하지 않고 새로 만들었습니다.
  * 상품 상태별 조회기능 , 다중 조건에 대한 검색기능, 상품 상태를 일괄변경할수 있는 멀티기능을 추가하였습니다.
    
&nbsp;
* 상품 등록

![4번영상 상품등록 - Clipchamp로 제작](https://github.com/user-attachments/assets/9e856fea-1e9f-499a-93a5-8867477f6c4f)

  * 개선전에는 여러개의 input을 사용하여 이미지를 등록하였고 개선후 상품 등록에 있어 다중 이미지 등록이 가능하게 하였습니다.
  * 부트스트랩 형식을 없애고 새로 뷰를 작성하였습니다.
  * 카테고리를 추가하였고 이전에는 ajax를 사용했다면 현재는 enum형태로 카테고리를 선언하여 사용하였습니다.
  * 상품관리에 들어가는 썸네일에 경우 thumbnailator 를 이용하여서 용량을 축소하였습니다.
&nbsp;
* 상품 crud
  
![5번-상품crud-Clipchamp로-제작](https://github.com/user-attachments/assets/c505b21c-af4a-4865-88b9-f31299899899)

  * 상품 수정시 상품명을 클릭하여 수정 페이지가 열리도록 하였습니다.
  * 이미지 변경이 없을시에는 기존 이미지를 그대로 사용하도록 로직을 추가하였습니다.
&nbsp;
* 상품 crud멀티 기능
  
![6번-상품crud멀티기능-Clipchamp로-제작](https://github.com/user-attachments/assets/a705a804-7166-41e2-8446-fc3d017ee956)

  * 체크박스의 value에 id를 담아서 체크된 상품에 대해서 상태변경을 할수 있도록 스크립트를 추가하였습니다.
&nbsp;
* 상품 검색
  
![7번영상-상품검색-Clipchamp로-제작 (1)](https://github.com/user-attachments/assets/3a1a7909-b439-4716-85e8-01b3a5b531e1)

  * JPQL을 사용하던 쿼리에서 querydsl를 사용하여 좀더 안전하고 가독성있는 코드가 되도록 변경하였습니다.
  * 다중조건에 해당하는 결과를 불러올수 있도록 코드를 작성하였습니다.
&nbsp;
* 상품 삭제
  
![8번상품삭제-Clipchamp로-제작](https://github.com/user-attachments/assets/630cb8aa-89a9-4d7a-ba4f-8600477e417e)

  * 체크박스가 선택된 후  멀티기능영역의 삭제버튼을 누르면 삭제가 될수 있도록 하였습니다.
&nbsp;
* 브랜드 crud
  
![9번-브랜드-crud-Clipchamp로-제작](https://github.com/user-attachments/assets/9f7bdb66-7ddf-4d54-b202-8fff3b68de32)

  * 기존의 단일 브랜드 쇼핑몰에서 여러개의 브랜드가 사용도리수 있도록 브랜드를 추가하였습니다.
  * 상품관리페이지와 동일하게 기본 CRUD가 동작하도록 하였습니다.
  * 브랜드 관리 페이지에서 상품수를 클릭하면 해당 브랜드에 속해있는 상품이 새창으로 오픈되도록 설정하였습니다.
&nbsp;
* 상품 리스트 조회
  
![10번-상품-리스트-조회-Clipchamp로-제작](https://github.com/user-attachments/assets/2cded756-1f8f-4208-a121-3ce1a0141e0f)

  * 쇼핑몰 메뉴에 들어가서 특정 카테고리를 선택하였을때 특정 카테고리에 해당하는 상품이 조회되도록 하였습니다.
  * 상품의 상태에 따라서 상품 리스트가 출력되로록 하였습니다.
&nbsp;
* 상품 상세 페이지 - 카트 담기 - 상세페이지 조개
   
![11-480-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/50437c51-7ace-45c5-8ad7-19c80c3441b0)


  * 기존의 부트스트랩기반의 뷰를 다시 실제 쇼핑몰과 유사하게 뷰를 작성하였습니다.
  * 상품 등록에서 상세이미지를 저장한것을 현재 쇼핑몰에 맞게 세로로 상세페이지를 보여줄수 있도록 하였습니다.
  * 주문하기,위시리스트,장바구니 담기 버튼을 만들고 기능을 구현하였습니다.
  * 상품 개수에 따른 가격 변동이 일어날수 있도록 스크립트를 작성하였습니다.

&nbsp;   
* 주문 후 - 결제
  
![12번-마지막-주문후-결제-Clipchamp로-제작](https://github.com/user-attachments/assets/6a7e6692-5eb6-439f-8775-c94944cd1f26)

  * 포트원 라이브러리를 이용해서 실제 결제 상황과 동일하게 작동하도록 코드를 작성하였습니다.
  * 영상에서 실제 롯데카드의 앱카드로 결제를 진행하고 있으며 쇼핑몰 주문페이지에 있던 그대로 가격이 결제 페이지에 전송됨 을 확인할 수 있습니다.
  * 결제가 완료된후 문제를 통해서 해당금액에 맞는 결제가 되었음을 알리는 문자가 전송됨을 확인하였습니다.





















