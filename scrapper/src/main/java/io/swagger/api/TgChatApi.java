/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.54).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */

package io.swagger.api;

import io.swagger.model.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
                            date = "2024-02-27T16:17:37.541889551Z[GMT]")
@Validated
public interface TgChatApi {

    @Operation(summary = "Удалить чат", description = "", tags = {})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),

        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),

        @ApiResponse(responseCode = "404",
                     description = "Чат не существует",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))})
    @RequestMapping(value = "/tg-chat/{id}",
                    produces = {"application/json"},
                    method = RequestMethod.DELETE)
    ResponseEntity<Void> tgChatIdDelete(
        @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id")
        Long id
    );

    @Operation(summary = "Зарегистрировать чат", description = "", tags = {})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),

        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409",
                     description = "Вы не можете повторно зарегистрировать чат",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))})
    @RequestMapping(value = "/tg-chat/{id}",
                    produces = {"application/json"},
                    method = RequestMethod.POST)
    ResponseEntity<Void> tgChatIdPost(
        @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id")
        Long id
    );

}

