package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.VM;
import dev.nelit.api.dto.response.VMResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vm_manager.VmManager;

@Mapper(componentModel = "spring")
public interface VMMapper {

    // entity -> response
    VMResponse toResponse(VM vm);

    // protobuf -> response
    @Mapping(source = "vmName",    target = "vmName")
    @Mapping(source = "uuid",      target = "uuid")
    @Mapping(source = "ipAddress", target = "ipAddress")
    VMResponse toResponse(VmManager.VMResponse proto);
}