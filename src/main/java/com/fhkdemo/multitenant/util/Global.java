package com.fhkdemo.multitenant.util;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Global {
    private String realm = "";
}
