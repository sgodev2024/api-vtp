package vn.coreplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@org.springframework.context.annotation.Import(vn.aicrm.retail.RetailConfiguration.class)
public class CorePlatformApplication {
  public static void main(String[] args) { SpringApplication.run(CorePlatformApplication.class, args); }
}
