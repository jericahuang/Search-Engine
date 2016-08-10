/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var request;


$(document).ready(function(){
    request = null;
    $("body").on("click","#searchButton",function(){
        if (request){ request.abort(); };
    
        var query = $("#searchInput").val();
        console.log(query);

        var dataToSend = {q: query};
        $.when(
            request = $.ajax({
                url:"performSearch",
                type: "post",
                data: dataToSend,
                dataType: "json"
            })
            .always(function(){
                console.log(request.responseText);
                jQuery("body").html(request.responseText);
                request = null;
            })
        
            ,getQandA()
        ).then(function() {
            getResults();
          // All have been resolved (or rejected), do your thing

        });
        
    });

    $(document).keypress(function(e) {
    if(e.which == 13) {
        event.preventDefault();
        getResults();
    }
});
});

function getQandA(){
    return "";
}


function getResults(){
    
    
    
}




function displayAnswer(response){
    console.log(response);
}