<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
  "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<#import "spring.ftl" as spring />

<#macro urlencode path="">
  <#if path != "">${response.encodeRedirectURL("?"+path)}</#if>
</#macro>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
    <title><@spring.message "title"/></title>
    <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8">

    <script type='text/javascript'><!--
      function reloadImage() {
        if (document.images) {
          var now = new Date();
          document.images.catpchaImage.src =
            '${baseweb}/module/local-login/generateCaptcha.do?'
              + now.getTime();
        }
      }
      //--></script>
  </head>

  <body>
  <div class="loginContainer">
    <h3><@spring.message "title"/></h3>
    <p><@spring.message "instructions"/></p>

    <form id='login' name='login' method="post"
      action="j_acegi_security_check">

      <span class="error" id="message">
        <#if ACEGI_SECURITY_LAST_EXCEPTION??>
        <!-- HACK: workaround for acegi storing the message in the session. -->
          ${ACEGI_SECURITY_LAST_EXCEPTION.message}
          ${request.session.removeAttribute('ACEGI_SECURITY_LAST_EXCEPTION')}
        </#if>
      </span>

      <span class="formfield">
        <label for="j_username">
          <@spring.message "username.label"/>
        </label>
        <input type='text' class="text" id="j_username" name="j_username"
          size="32" tabindex="1" accesskey
            ='<@spring.message code="username.accesskey"/>'/>
      </span>

      <span class="formfield">
        <label for="j_password">
          <@spring.message "password.label"/>
        </label>
        <#--
        NOTE: Certain browsers will offer the option of caching passwords for a
        user. There is a non-standard attribute, "autocomplete" that when set
        to "off" will tell certain browsers not to prompt to cache credentials.
        For more information, see the following web page:
        http://www.geocities.com/technofundo/tech/web/ie_autocomplete.html
        -->
        <input type="password" class="text" id="j_password" name="j_password"
          size="32" tabindex="2" accesskey
          ='<@spring.message "password.accesskey"/>'/>
      </span>

      <#if showCaptcha!false>
        <span class="formfield">
          <label for="captcha">
            <@spring.message "captcha.instructions"/>
          </label>
          <input type="text" class="text" id="j_captcha_response"
            name="j_captcha_response" tabindex="3" value=""/>
          <img alt='captcha' name='catpchaImage'
            src="${baseweb}/module/local-login/generateCaptcha.do" />
          <input type="submit" class="btn"
            onclick='reloadImage();return false;' accesskey="l"
            value='<@spring.message "captcha.instructions"/>'
            tabindex="4" />
        </span>
      </#if>

      <div class="buttons clearfix">
        <input name="loginButton" type="submit" class="btn rightMargin"
          accesskey="l" value='<@spring.message "button.submit"/>'
          tabindex="5" />
        <input type="reset" class="btn" accesskey="c"
          value='<@spring.message "button.clear"/>'
          tabindex="6" />
        <#list additionalButtons as button>
          <#assign buttonInfo = button?matches(" *(.*) *\\| *(.*) *")>
          <input type="submit" class="btn" value='${buttonInfo?groups[1]}'
            onclick='window.location="${baseweb}${buttonInfo?groups[2]}";return false;'/>
        </#list>
      </div>

    </form>
  </div>
    <!-- vim: set ts=2 sw=2 et ai: -->
  </body>
</html>

