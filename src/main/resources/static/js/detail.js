window.onload=function(){
// JavaScript로 동작 구현
    const minusButton = document.getElementById("minus");
    const plusButton = document.getElementById("plus");
    const numberInput = document.getElementById("count");


    minusButton.addEventListener("click", () => {
        let currentValue = parseInt(numberInput.value);
        if (currentValue > 1) {
            currentValue--;
            numberInput.value = currentValue;
            if (currentValue === 1) {
                minusButton.disabled = true;
            }
        }
        calculateTotalPrice(); // 값 변경 후 총 가격 업데이트
    });

    plusButton.addEventListener("click", () => {
        let currentValue = parseInt(numberInput.value);
        currentValue++;
        numberInput.value = currentValue;
        if (currentValue > 1) {
            minusButton.disabled = false;
        }
        calculateTotalPrice(); // 값 변경 후 총 가격 업데이트
    });

    // count 변경 시 총 가격 업데이트
    numberInput.addEventListener("change", function () {
        calculateTotalPrice();
    });

    function calculateTotalPrice() {
        var count = parseInt(numberInput.value);
        var price = parseInt(document.getElementById("itemPrice").value);
        var totalPrice = price * count;

        // 결과를 업데이트하는 부분
        var resultPriceElement = document.querySelector(".result_price");
        resultPriceElement.textContent = totalPrice.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",") + '원';
        // 자리수 정규식 totalPrice에 tostirng에 replace
    }
    //jquery시작  TODO:해당 파일에 있는 order() 를 읽지 못하고 있음
    $(document).ready(function() {
        function order() {
            console.log("start_order")
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");

            var url = "/order";
            var paramData = {
                itemId: $("#itemId").val(),
                count: $("#count").val()
            };

            var param = JSON.stringify(paramData);

            $.ajax({
                url: url,
                type: "POST",
                contentType: "application/json",
                data: param,
                beforeSend: function (xhr) {
                    /* 데이터를 전송하기 전에 헤더에 csrf값을 설정 */
                    xhr.setRequestHeader(header, token);
                },
                dataType: "json",
                cache: false,
                success: function (result, status) {
                    alert("주문이 완료 되었습니다.");
                    location.href = '/';
                },
                error: function (jqXHR, status, error) {

                    //
                    if (jqXHR.status == '401') {
                        alert('로그인 후 이용해주세요');
                        location.href = '/members/login';
                    } else {
                        alert(jqXHR.responseText);
                    }

                }
            });
        }
    });
    function addCart(){
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var url = "/cart";
        var paramData = {
            itemId : $("#itemId").val(),
            count : $("#count").val()
        };

        var param = JSON.stringify(paramData);

        $.ajax({
            url      : url,
            type     : "POST",
            contentType : "application/json",
            data     : param,
            beforeSend : function(xhr){
                /* 데이터를 전송하기 전에 헤더에 csrf값을 설정 */
                xhr.setRequestHeader(header, token);
            },
            dataType : "json",
            cache   : false,
            success  : function(result, status){
                alert("상품을 장바구니에 담았습니다.");
                location.href='/';
            },
            error : function(jqXHR, status, error){

                if(jqXHR.status == '401'){
                    alert('로그인 후 이용해주세요');
                    location.href='/members/login';
                } else{
                    alert(jqXHR.responseText);
                }

            }
        });
    }



}