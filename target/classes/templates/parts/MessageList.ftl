<#include "security.ftl">
<div class="card-columns">
    <#list messages as message>
        <div class="card my-3">
            <#if message.filename??>
                <img src="/img/${message.filename}" class="card-img-top">
            </#if>
            <div class="m-2">
                <span>${message.text}</span><br/>
                <i>#${message.tag}</i>
            </div>
            <div class="card-footer text-muted">
                <a href="/user-messages/${message.author.id}">${message.authorName}</a>
                <#if message.author.id  == currentUserId>
                <a class="btn btn-primary" href="/user-messages/${message.author.id}?message=${message.id}">Edit</a>
                    <a class="btn btn-primary"href="/delete/${message.id}">Delete</a>
<#--                <#if !user??><a class="btn btn-primary"href="/share/${message.id}">Share</a><#else></#if>-->
                </#if>
                <#--<#if message.author.id  != currentUserId ><a class="btn btn-primary" href="/user-messages/${user.id}">Share</a>
                <#else>&lt;#&ndash;${message.authorName}&ndash;&gt;</#if>-->

            </div>

        </div>
    <#else>
        No message
    </#list>
</div>