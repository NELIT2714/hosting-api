package dev.nelit.api.mappers;

import dev.nelit.api.domain.entity.vm.Vm;
import dev.nelit.api.dto.response.VM.VmResponse;
import org.mapstruct.Mapper;
import vm_manager.VmManager;

@Mapper(componentModel = "spring")
public interface VMMapper {

    // entity -> response
    VmResponse toResponse(Vm vm);

    // protobuf -> response
//    @Mapping(source = "vmName",    target = "vmName")
//    @Mapping(source = "uuid",      target = "uuid")
//    @Mapping(source = "ipAddress", target = "ipAddress")
    VmResponse toResponse(VmManager.VMResponse proto);
}