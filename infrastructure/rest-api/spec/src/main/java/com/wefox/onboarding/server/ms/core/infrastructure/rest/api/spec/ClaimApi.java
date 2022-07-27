package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.spec;

import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimRequest;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimStatusUpdateRequest;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimUpdateRequest;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response.ClaimResponse;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response.ResourcePageClaim;
import com.wefox.server.lib.common.api.error.ErrorDTO;
import com.wefox.server.lib.common.api.pagination.PageDTO;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value = "/claims", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Claims")
@OpenAPIDefinition(
    info =
        @Info(
            title = "Claims Domain API",
            version = "v1",
            description = "Context Claim API",
            contact =
                @Contact(
                    url = "www.wefoxgroup.com/support",
                    name = "Claims Domain Team",
                    email = "claims.insurance.group@wefox.com")))
@SecurityRequirement(name = "wefox_id", scopes = "onboarding-server-ms-core")
public interface ClaimApi {

  String notFoundDesc = "The claim with the specified claimId was not found.";
  String notFoundJsonExample =
      "{ \"status\": \"404\", \"code\": \"CLAIM_DOM_003\", \"title\": \"Claim not found\", \"detail\": \"Claim not found\", \"invalid_params\": []}";
  String unprocessableDesc = "Unprocessable entity. Business validation failed.";

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get Claims",
      description =
          "Returns a list of all claims.\n\n > You can filter the results list by the following query parameters: contract ID, offer ID, or account ID.",
      parameters = {
        @Parameter(name = "account_id", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
        @Parameter(name = "contract_id", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
        @Parameter(name = "offer_id", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
        @Parameter(
            name = "page_number",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "integer", minimum = "1", defaultValue = "1")),
        @Parameter(
            name = "page_size",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "number", minimum = "1", defaultValue = "25"))
      },
      operationId = "findClaims")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    schema =
                        @Schema(
                            implementation = ResourcePageClaim.class,
                            name = "ResourcePageClaim"),
                    examples = {
                      @ExampleObject(
                          name = "Example List of Claims",
                          value =
                              "{ \"page_number\": 0, \"page_size\": 25, \"total_elements\": 27, \"total_pages\": 2, \"content\": [\n"
                                  + "    { \"description\": \"null\", \"type\": \"070101\", \"status\": \"CREATED\", \"claim_id\": \"CLM-444445\", \"event_date\": \"2021-02-06T12:48:19Z\", \"entry_date\": \"2021-04-23T09:12:03.859022Z\", \"notification_date\": \"2021-02-25T09:29:15.453443Z\", \"place_of_event\": \"null\", \"contract_id\": \"CON-566777\", \"offer_id\": \"null\", \"account_id\": \"ACC-657456\", \"coverage_id\": \"COV-425424\", \"product_id\": \"IP-333333\", \"insurance_id\": \"INS-842094\", \"without_coverage\": false }, { \"description\": \"null\", \"type\": \"070101\", \"status\": \"CREATED\", \"claim_id\": \"SHOWCASE-9\", \"event_date\": \"2021-02-06T12:48:19.035853Z\", \"entry_date\": \"2021-03-30T21:43:42.860184Z\", \"notification_date\": \"2021-02-25T09:29:15.453443Z\", \"place_of_event\": \"null\", \"contract_id\": \"CON-566777\", \"offer_id\": \"null\", \"account_id\": \"ACT_DEV_MS_ACCOUNT_E2\", \"coverage_id\": \"COV-425424\", \"product_id\": \"IP-333333\", \"insurance_id\": \"INS-842094\", \"without_coverage\": false, \"symass_id\": \"DE12229\" } ] }"),
                      @ExampleObject(
                          name = "Example Empty List",
                          value =
                              "{ \"page_number\": 0, \"page_size\": 0, \"total_elements\": 0, \"total_pages\": 0, \"content\": [] }")
                    })),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized.",
            content = @Content(schema = @Schema))
      })
  PageDTO<ClaimResponse> getClaims(
      @RequestParam(value = "account_id", required = false) String accountId,
      @RequestParam(value = "contract_id", required = false) String contractIdd,
      @RequestParam(value = "offer_id", required = false) String offerId,
      @RequestParam(value = "page_number", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(value = "page_size", required = false, defaultValue = "25") int pageSize);

  @GetMapping(value = "/{claimId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Get Claim by ID",
      operationId = "findOneClaim",
      description = "Returns the claim specified by its ID.",
      parameters = {
        @Parameter(
            name = "claimId",
            in = ParameterIn.PATH,
            description = "The ID of the claim to be retrieved, or updated.",
            schema = @Schema(type = "string"),
            required = true)
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    schema = @Schema(implementation = ClaimResponse.class),
                    examples = {
                      @ExampleObject(
                          name = "Example",
                          description = "Example",
                          value =
                              "{ \"claim_id\": \"string\", \"event_date\": \"2020-08-24T14:15:22Z\", \"entry_date\": \"2020-08-24T14:15:22Z\", \"notification_date\": \"2020-08-24T14:15:22Z\", \"place_of_event\": \"string\", \n"
                                  + "  \"description\": \"string\", \"contract_id\": \"string\", \"offer_id\": \"string\", \"account_id\": \"string\", \"type\": \"Break and enter\", \"coverage_id\": \"string\", \"status\": \"CREATED\", \"product_id\": \"string\", \"insurance_id\": \"string\", \"without_coverage\": true }")
                    })),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized.",
            content = @Content(schema = @Schema)),
        @ApiResponse(
            responseCode = "404",
            description = notFoundDesc,
            content =
                @Content(
                    schema = @Schema(implementation = ErrorDTO.class),
                    examples =
                        @ExampleObject(
                            name = "Example",
                            description = "Example",
                            value = notFoundJsonExample)))
      })
  ClaimResponse getClaimById(@PathVariable String claimId);

  @PutMapping(value = "/{claimId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      description = "Updates a claim specified by its ID.",
      operationId = "put-claims-claimId",
      summary = "Update a Claim specified by ID",
      parameters =
          @Parameter(
              name = "claimId",
              in = ParameterIn.PATH,
              description = "The ID of the claim to be retrieved, or updated.",
              schema = @Schema(type = "string"),
              required = true))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    schema = @Schema(implementation = ClaimResponse.class),
                    examples = {
                      @ExampleObject(
                          name = "Example",
                          value =
                              "{ \"claim_id\": \"9876543211111\", \"event_date\": \"2021-04-13T14:15:22Z\", \"entry_date\": \"2021-05-03T14:39:35.55681Z\", \"notification_date\": \"2021-04-14T14:15:22Z\", \"place_of_event\": \"null\", \"description\": \"string\", \"contract_id\": \"contract_id_8\", \"offer_id\": \"null\", \"account_id\": \"ACT_DEV_MS_ACCOUNT_E2E\", \"type\": \"070101\", \"coverage_id\": \"COV-425424\", \"status\": \"CREATED\", \"product_id\": \"IP-333333\", \"insurance_id\": \"INS-842094\", \"without_coverage\": true }")
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request. Basic validation failed.",
            content =
                @Content(
                    schema = @Schema(implementation = ErrorDTO.class),
                    examples =
                        @ExampleObject(
                            name = "Example",
                            value =
                                "{ \"status\": \"400\", \"code\": \"COMMON_001\", \"title\": \"Request parsing failed\", \"detail\": \"Detailed Java exception\" }"))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized.",
            content = @Content(schema = @Schema)),
        @ApiResponse(
            responseCode = "404",
            description = notFoundDesc,
            content =
                @Content(
                    schema = @Schema(implementation = ErrorDTO.class),
                    examples = @ExampleObject(name = "Example", value = notFoundJsonExample))),
        @ApiResponse(
            responseCode = "422",
            description = unprocessableDesc,
            content =
                @Content(
                    schema = @Schema(implementation = ErrorDTO.class),
                    examples =
                        @ExampleObject(
                            name = "Example",
                            value =
                                "{ \"status\": \"422\", \"code\": \"CLAIM_DOM_008\", \"title\": \"Update claim validation error\", \"detail\": \"One or more fields of the request contain invalid values.\", \"invalid_params\": [ { \"name\": \"notification_date\", \"reason\": \"Should be before event_date.\" }, { \"name\": \"event_date\", \"reason\": \"Future dates are not allowed.\" } ] }")))
      })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = {
        @Content(
            examples =
                @ExampleObject(
                    name = "Example",
                    value =
                        "{\"event_date\":\"2019-08-24T14:15:22Z\",\"notification_date\":\"2019-08-24T14:15:22Z\",\"place_of_event\":\"string\",\"description\":\"string\",\"type\":\"Break and enter\"}"))
      },
      required = true)
  ClaimResponse updateClaim(
      @PathVariable String claimId, @RequestBody @Valid ClaimUpdateRequest claimUpdateRequest);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      description = "Creates a new claim.",
      summary = "Create a Claim",
      operationId = "createClaim")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created.",
            content =
                @Content(
                    schema = @Schema(implementation = ClaimResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Example",
                            value =
                                "{ \"description\": \"null\", \"type\": \"072200\", \"status\": \"CREATED\", \"claim_id\": \"CLM-444445\", \"event_date\": \"2021-02-06T12:48:19Z\", \"entry_date\": \"2021-04-23T09:12:03.859022Z\", \"notification_date\": \"2021-02-25T09:29:15.453443Z\", \"place_of_event\": \"null\", \"contract_id\": \"CON-566777\", \"offer_id\": \"null\", \"account_id\": \"ACC-657456\", \"coverage_id\": \"COV-425424\", \"product_id\": \"IP-333333\", \"insurance_id\": \"INS-842094\", \"without_coverage\": false, \"symass_id\": \"DE12229\" }"))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request. Basic validation failed.",
            content =
                @Content(
                    schema = @Schema(implementation = ErrorDTO.class),
                    examples = {
                      @ExampleObject(
                          name = "Request Parsing Failed",
                          value =
                              "{ \"status\": 400, \"code\": \"COMMON_001\", \"title\": \"Request parsing failed\", \"detail\": \"Detailed Java exception\" }"),
                      @ExampleObject(
                          name = "Missing Required Parameters",
                          value =
                              "{ \"status\": 400, \"code\": \"COMMON_002\", \"title\": \"Invalid input format\", \"detail\": \"At least one input parameter is invalid\", \"invalid_params\": [ { \"name\": \"account_id\", \"reason\": \"Must not be null.\" }, { \"name\": \"product_id\", \"reason\": \"Must not be null.\" } ] }")
                    })),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized.",
            content = @Content(schema = @Schema)),
        @ApiResponse(
            responseCode = "422",
            description = unprocessableDesc,
            content =
                @Content(
                    schema = @Schema(implementation = ErrorDTO.class),
                    examples =
                        @ExampleObject(
                            name = "Example",
                            value =
                                "{ \"status\": 422, \"code\": \"CLAIM_DOM_005\", \"title\": \"Create claim validation error\", \"detail\": \"One or more fields of the request contain invalid values.\", \"invalid_params\": [ { \"name\": \"notification_date\", \"reason\": \"Should be before event_date.\" }, { \"name\": \"event_date\", \"reason\": \"Future dates are not allowed.\" } ] }")))
      })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = {
        @Content(
            examples =
                @ExampleObject(
                    name = "Example",
                    value =
                        "{\"claim_id\":\"987654321111111111\",\"event_date\":\"2021-04-13T14:15:22Z\",\"notification_date\":\"2021-04-14T14:15:22Z\",\"contract_id\":\"contract_id_8\",\"account_id\":\"ACT_DEV_MS_ACCOUNT_E2E\",\"type\":\"072200\",\"product_id\":\"product_id_8\",\"without_coverage\":false,\"symass_id\":\"DE12229\"}"))
      },
      required = true)
  ClaimResponse createClaim(@RequestBody @Valid ClaimRequest claimRequest);

  @PutMapping(value = "/{claimId}/status")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      description =
          "Update the status of the claim specified by its ID.\n\n"
              + "Specify the status in the request body.\n\n"
              + "**Possible status values**:\n"
              + "- CREATED\n"
              + "- PAID_OUT\n"
              + "- REJECTED\n"
              + "- UNDER_REVIEW\n"
              + "- WITHDRAWN\n",
      operationId = "statusUpdate",
      summary = "Update Claim Status",
      parameters =
          @Parameter(
              name = "claimId",
              in = ParameterIn.PATH,
              description = "The ID of the claim to be retrieved, or updated.",
              schema = @Schema(type = "string"),
              required = true))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    schema = @Schema(implementation = ClaimResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Example",
                            value =
                                "{ \"claim_id\": \"string\", \"event_date\": \"2019-08-24T14:15:22Z\", \"entry_date\": \"2019-08-24T14:15:22Z\", \"notification_date\": \"2019-08-24T14:15:22Z\", \"place_of_event\": \"string\", \"description\": \"string\", \"contract_id\": \"string\", \"offer_id\": \"string\", \"account_id\": \"string\", \"type\": \"string\", \"coverage_id\": \"string\", \"status\": \"PAID_OUT\", \"product_id\": \"string\", \"insurance_id\": \"string\", \"without_coverage\": true }"))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request. Basic validation failed.",
            content =
                @Content(
                    schema = @Schema(implementation = ErrorDTO.class),
                    examples =
                        @ExampleObject(
                            name = "Example",
                            value =
                                "{ \"status\": \"400\", \"code\": \"COMMON_001\", \"title\": \"Request parsing failed\", \"detail\": \"Detailed Java exception\" }"))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized.",
            content = @Content(schema = @Schema)),
        @ApiResponse(
            responseCode = "404",
            description = notFoundDesc,
            content =
                @Content(
                    schema = @Schema(implementation = ErrorDTO.class),
                    examples = @ExampleObject(name = "Example", value = notFoundJsonExample))),
        @ApiResponse(
            responseCode = "422",
            description = unprocessableDesc,
            content =
                @Content(
                    schema = @Schema(implementation = ErrorDTO.class),
                    examples =
                        @ExampleObject(
                            name = "Example",
                            value =
                                "{ \"status\": \"422\", \"code\": \"CLAIM_DOM_007\", \"title\": \"Invalid claim status update\", \"detail\": \"Desired status transition is not allowed\" }")))
      })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = {
        @Content(examples = @ExampleObject(name = "Example", value = "{\"status\":\"PAID_OUT\"}"))
      },
      required = true)
  ClaimResponse updateClaimStatus(
      @PathVariable String claimId,
      @RequestBody @Valid ClaimStatusUpdateRequest claimStatusUpdateRequest);
}
