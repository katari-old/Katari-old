/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.util.Date;

import com.globant.katari.shindig.domain.SampleUser;
import org.apache.shindig.social.opensocial.model.Activity;

public class KatariActivityTest {

  private String gadgetXmlUrl = "file:///" + new File(
      "src/test/resources/FullSampleGadget.xml").getAbsolutePath();

  @Test
  public void testActivity_copy() {
    Activity activity = new KatariActivity();
    SampleUser user = new SampleUser("test");

    Date updated = new Date();

    activity.setBodyId("body-id");
    activity.setBody("body");
    activity.setExternalId("external-id");
    activity.setTitleId("title-id");
    activity.setTitle("title");
    activity.setUpdated(updated);
    activity.setPriority(1.0f);
    activity.setStreamFaviconUrl("favicon-url");
    activity.setStreamSourceUrl("stream-source-url");
    activity.setStreamTitle("stream-title");
    activity.setStreamUrl("stream-url");
    activity.setUrl("url");

    KatariActivity newActivity = new KatariActivity(new Date().getTime(),
        new Application(gadgetXmlUrl), user, activity);

    assertThat(newActivity.getAppId(), is(gadgetXmlUrl));
    assertThat(newActivity.getBodyId(), is("body-id"));
    assertThat(newActivity.getBody(), is("body"));
    assertThat(newActivity.getExternalId(), is("external-id"));
    assertThat(newActivity.getTitleId(), is("title-id"));
    assertThat(newActivity.getTitle(), is("title"));
  }
}

