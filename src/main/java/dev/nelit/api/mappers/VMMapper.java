package dev.nelit.api.mappers;

import dev.nelit.api.dto.response.VMResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vm_manager.VmManager;

@Mapper(componentModel = "spring")
public interface VMMapper {

    @Mapping(source = "vmName",    target = "vmName")
    @Mapping(source = "uuid",      target = "uuid")
    @Mapping(source = "ipAddress", target = "ipAddress")
    @Mapping(source = "status",    target = "status")
    VMResponse toResponse(VmManager.VMResponse proto);
}