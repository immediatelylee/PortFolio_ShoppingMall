// 모달창
// $(function(){
//
//     $(".modal_btn").click(function(){
//         $(".modal2").fadeIn();
//     });
//
//     $(".modal_close").click(function(){
//         $(".modal2").fadeOut();
//     });
//
//     $(".modal_commit").click(function(){
//         $(".modal2").fadeOut();
//     });
//
//     $(".modal_btn2").click(function(){
//         $(".modal2_second").fadeIn();
//     });
//
//     $(".modal_close2").click(function(){
//         $(".modal2_second").fadeOut();
//     });
//
//     $(".modal_commit2").click(function(){
//         $(".modal2_second").fadeOut();
//     });
//
//     $(".coupon_modal_btn").click(function(){
//         $(".coupon_modal").fadeIn();
//     });
//
//     $(".coupon_modal_close").click(function(){
//         $(".coupon_modal").fadeOut();
//     });
//
//
// });


// 포토리뷰 이미지 클릭시 모달창
$(function(){

    $(".modal_click").click(function(){
        $(".modal3").fadeIn();
    });

    $(".modal_back").click(function(){
        $(".modal3").fadeOut();
    });

    $(".modal_commit").click(function(){
        $(".modal3").fadeOut();
    });

});


// 포토리뷰 이미지 클릭시 모달창
$(function(){

    $(".modal4_click").click(function(){
        $(".modal4").fadeIn();
    });

    $(".modal4_cart").click(function(){
        $(".modal4").fadeOut();
    });

    $(".modal4_commit").click(function(){
        $(".modal4").fadeOut();
    });

    $(".modal4_close").click(function(){
        $(".modal4").fadeOut();
    });

});


// 로그인 모달창
$(function(){

    $(".common_layer_btn").click(function(){
        $(".common_layer").fadeIn();
    });

    $(".common_layer_close").click(function(){
        $(".common_layer").fadeOut();
    });

    $(".common_layer_btn2").click(function(){
        $(".common_layer2").fadeIn();
    });

    $(".common_layer_close2").click(function(){
        $(".common_layer2").fadeOut();
    });

});

// 장바구니이동 모달창
$(function(){

    $(".cart_modal_btn").click(function(){
        $(".cart_modal").fadeIn();
    });

    $(".cart_modal_close").click(function(){
        $(".cart_modal").fadeOut();
    });

    // 모달을 시간이 지나면 자동으로 숨김 1000초가 1초
    setTimeout(function () {
        $(".cart_modal").fadeOut();
    }, 5000);
});


//  모달4 수량버튼
function count(type)  {
    // 결과를 표시할 element
    const resultElement = document.getElementById('result');

    // 현재 화면에 표시된 값
    let number = parseInt(resultElement.innerText);

    // 더하기/빼기

    if(type === 'plus') {
        number = number + 1;
    }else if(type === 'minus')  {
        number > 0 ? number = number - 1 : null;
    }

    // 결과 출력
    resultElement.innerText = number.toString();
}