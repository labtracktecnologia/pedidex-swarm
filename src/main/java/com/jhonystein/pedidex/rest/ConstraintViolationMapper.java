package com.jhonystein.pedidex.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        Map<String, List<String>> erros = new HashMap<>();

        for (ConstraintViolation<?> v : e.getConstraintViolations()) {
            String value = Optional.ofNullable(v.getInvalidValue())
                    .orElse("NULL").toString();
            erros.put(printPropertyPath(v.getPropertyPath()), Arrays.asList(value, v.getMessage()));
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(erros)
                .build();
    }

    private String printPropertyPath(Path path) {
        if (path == null) {
            return "UNKNOWN";
        }

        String propertyPath = "";
        Path.Node parameterNode = null;
        // Construct string representation of property path.
        // This will strip away any other nodes added by RESTEasy (method, parameter, ...).
        for (Path.Node node : path) {
            if (node.getKind() == ElementKind.PARAMETER) {
                parameterNode = node;
            }

            if (node.getKind() == ElementKind.PROPERTY) {
                if (!propertyPath.isEmpty()) {
                    propertyPath += ".";
                }
                propertyPath += node;
            }
        }

        if (propertyPath.isEmpty() && parameterNode != null) {
            // No property path constructed, assume this is a validation failure on a request parameter.
            propertyPath = parameterNode.toString();
        }
        return propertyPath;
    }
}
