<#import "spring.ftl" as spring />

<html>

  <head>
    <title>Change password</title>
  </head>

  <body>
    <h3>Change password</h3>
    <form id="changePassword" name="changePassword" method="POST"
      action="changePassword.do">
      <#setting number_format="computer">

      <span class="error" id="message">
        <@spring.bind "command.*"/>
        <@spring.showErrors  "<br/>" />
      </span>
      
      <div class="clearfix column left">
      
        <div class="column left">  

          <@spring.formHiddenInput "command.userId" />

          <#if command.userId == command.me.id>
            <!-- Changing other user password, do not ask for the old password
                 -->
            <span class="formfield">
              <label for="oldPassword">Old Password</label>
              <@spring.formPasswordInput "command.password.oldPassword" />
            </span>
          </#if>

          <span class="formfield">
            <label for="password">New Password</label>
            <@spring.formPasswordInput "command.password.newPassword" />
          </span>

          <span class="formfield">
            <label for="passwordConfirm">Confirm New Password</label>
            <@spring.formPasswordInput "command.password.confirmedPassword" />
          </span>       

          <input type="hidden" name='backTo' value="${command.backTo!''}"/>

          <input class="btn rightMargin" type="submit" value="Save"/>
          <input class="btn" type="submit" value="Cancel"
            <#if command.backTo?? && command.backTo?trim?length != 0>
              onclick="window.location=
                '${request.contextPath}/${command.backTo}';return false;"
            <#else>
              onclick="window.location=
                '${request.contextPath}/users.do';return false;"
            </#if>
            />
              
        </div>
          
      </div>
    </form>
  </body>
</html>

<!-- vim: set ts=2 et sw=2 ai ft=xml: -->

