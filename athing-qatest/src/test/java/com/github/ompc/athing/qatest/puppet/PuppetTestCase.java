package com.github.ompc.athing.qatest.puppet;

import com.github.ompc.athing.product.IThingProductProperty;
import com.github.ompc.athing.product.IThingProductService;
import com.github.ompc.athing.product.IThingToken;
import com.github.ompc.athing.product.ThingProductException;
import com.github.ompc.athing.product.event.ThingReplyPropertySetEvent;
import com.github.ompc.athing.product.event.ThingReplyServiceReturnEvent;
import com.github.ompc.athing.product.event.ThingReportDataEvent;
import com.github.ompc.athing.product.event.ThingReportPropertiesEvent;
import com.github.ompc.athing.qatest.puppet.domain.Echo;
import com.github.ompc.athing.qatest.puppet.domain.PersonalInfo;
import com.github.ompc.athing.qatest.puppet.domain.Result;
import com.github.ompc.athing.qatest.puppet.domain.Tick;
import com.github.ompc.athing.thing.ThingCodes;
import com.github.ompc.athing.thing.ThingException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

public class PuppetTestCase extends PuppetSupport {

    @Test
    public void test$service$echo() throws ThingProductException {

        final String words = UUID.randomUUID().toString();
        final IThingProductService<Echo, Result<String>> thingProductService = puppetThingProduct.getThingProductService("echo");
        final IThingToken<Result<String>> token = thingProductService.service(THING_ID, new Echo(words));

        Assert.assertNotNull(token.getData());
        Assert.assertEquals(PRODUCT_ID, token.getProductId());
        Assert.assertEquals(THING_ID, token.getThingId());
        Assert.assertNotNull(token.getToken());
        Assert.assertTrue(token.getData().isSuccess());
        Assert.assertEquals(words + "by-SYNC", token.getData().getData());
    }

    @Test
    public void test$service$async_echo() throws ThingProductException, InterruptedException {

        final String words = UUID.randomUUID().toString();
        final IThingProductService<Echo, Result<String>> thingProductService = puppetThingProduct.getThingProductService("async_echo");
        final IThingToken<Result<String>> token = thingProductService.service(THING_ID, new Echo(words));
        final ThingReplyServiceReturnEvent event = takeThingEventFromQueue();
        final Result<String> resultFromReply = event.getData();

        Assert.assertNull(token.getData());
        Assert.assertEquals(PRODUCT_ID, event.getProductId());
        Assert.assertEquals(THING_ID, event.getThingId());
        Assert.assertEquals("async_echo", event.getServiceId());
        Assert.assertTrue(event.getOccurTimestamp() > 0);
        Assert.assertEquals(ThingCodes.OK, event.getCode());
        Assert.assertEquals(token.getToken(), event.getToken());
        Assert.assertTrue(resultFromReply.isSuccess());
        Assert.assertEquals(words + "by-ASYNC", resultFromReply.getData());

    }

    @Test
    public void test$report_data$tick() throws ThingException, InterruptedException {
        final Date now = new Date();
        puppetThingReporter.reportThingData("tick", new Tick(now, now.toString()));
        final ThingReportDataEvent event = takeThingEventFromQueue();
        Assert.assertEquals(PRODUCT_ID, event.getProductId());
        Assert.assertEquals(THING_ID, event.getThingId());
        Assert.assertEquals("tick", event.getDataId());
        final Tick tickFromEvent = event.getData();
        Assert.assertEquals(now.getTime(), tickFromEvent.getTimestamp().getTime());
        Assert.assertEquals(now.toString(), tickFromEvent.getLocalTime());
    }

    @Test
    public void test$property_change$age() throws ThingProductException, InterruptedException {
        final IThingProductProperty<Integer> ageThingProductProperty = puppetThingProduct.getThingProductProperty("age");
        final IThingToken<Void> token = ageThingProductProperty.set(THING_ID, 35);
        final ThingReplyPropertySetEvent event = takeThingEventFromQueue();
        Assert.assertEquals(35, puppet.getAge());
        Assert.assertEquals(PRODUCT_ID, event.getProductId());
        Assert.assertEquals(THING_ID, event.getThingId());
        Assert.assertEquals(token.getToken(), event.getToken());
        Assert.assertTrue(event.getPropertyIds().contains("age"));
    }


    @Test
    public void test$property_report$personal_info() throws InterruptedException, ThingException {
        final PersonalInfo personalInfo = new PersonalInfo();
        personalInfo.setIdCard("450106111111111111");
        personalInfo.setBirthday(new Date());
        puppet.setPersonalInfo(personalInfo);
        puppetThingReporter.reportThingProperty("personal_info");

        final ThingReportPropertiesEvent event = takeThingEventFromQueue();
        final PersonalInfo personalInfoFromEvent = event.getPropertyValue("personal_info").getValue();
        Assert.assertEquals(personalInfo.getIdCard(), personalInfoFromEvent.getIdCard());
        Assert.assertEquals(personalInfo.getBirthday(), personalInfoFromEvent.getBirthday());
        Assert.assertEquals(PRODUCT_ID, event.getProductId());
        Assert.assertEquals(THING_ID, event.getThingId());
    }

}
