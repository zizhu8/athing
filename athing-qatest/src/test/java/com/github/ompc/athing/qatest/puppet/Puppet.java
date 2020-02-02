package com.github.ompc.athing.qatest.puppet;

import com.github.ompc.athing.qatest.puppet.domain.ContactInfo;
import com.github.ompc.athing.qatest.puppet.domain.PersonalInfo;
import com.github.ompc.athing.qatest.puppet.domain.Result;
import com.github.ompc.athing.thing.annotation.Thing;
import com.github.ompc.athing.thing.annotation.ThingParameter;
import com.github.ompc.athing.thing.annotation.ThingProperty;
import com.github.ompc.athing.thing.annotation.ThingService;

import static com.github.ompc.athing.thing.annotation.ThingService.Mode.ASYNC;

@Thing
public class Puppet {

    private PersonalInfo personalInfo;

    @ThingProperty("contact_info")
    private ContactInfo contactInfo;

    @ThingProperty
    private int age;

    @ThingProperty
    private String name;

    @ThingService
    public Result<String> echo(@ThingParameter("words") String words) {
        return new Result<>(true, words + "by-SYNC");
    }

    @ThingService(value = "async_echo", mode = ASYNC)
    public Result<String> asyncEcho(@ThingParameter("words") String words) {
        return new Result<>(true, words + "by-ASYNC");
    }

    @ThingProperty("personal_info")
    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
