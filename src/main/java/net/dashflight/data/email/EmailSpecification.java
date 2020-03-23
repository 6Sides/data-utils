package net.dashflight.data.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailSpecification {

    private String recipient, subject, body;

}
