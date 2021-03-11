package demo.rabbit.retry.backoff.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

@Value
@Getter
@ToString
@SuppressWarnings("serial")
public class Request implements Serializable {

  private String key;


//  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
//  private LocalDate dateTime;

  @JsonCreator
  @lombok.Builder(builderClassName = "Builder")
  Request(@JsonProperty("key") String key
//      @JsonProperty("dateTime")
//      @JsonFormat(pattern = "yyyy-MM-dd")
//          LocalDate dateTime
  ) {
    this.key = key;
//    this.dateTime = dateTime;
  }
}
