package com.arka.usuario_service.service;

import com.arka.usuario_service.DTO.ProovedorDto;
import com.arka.usuario_service.Mapper.ProovedorMapper;
import com.arka.usuario_service.excepcion.ProveedorNotFoundException;
import com.arka.usuario_service.model.Proovedores;
import com.arka.usuario_service.repositorio.ProovedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedoresService {

    private final ProovedorRepository repository;
    private final ProovedorMapper mapper;

    public ProveedoresService(ProovedorRepository repository, ProovedorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Proovedores create(ProovedorDto proovedor){

        if (proovedor.getNombre().isBlank() || proovedor.getNombre()== null){
            throw new IllegalArgumentException("nombre can not be blank");
        }
        if (repository.existsByNombre(proovedor.getNombre().trim())){
            throw  new IllegalArgumentException("proovedor already registered with that name");
        }
        if (proovedor.getCaracteristicas().isBlank()||proovedor.getCaracteristicas()==null){
            throw new IllegalArgumentException("caracteristicas can not be blank");
        }
        if (proovedor.getTelefono().isBlank()||proovedor.getTelefono()==null||proovedor.getTelefono().length()<10){
            throw new IllegalArgumentException("phone number can not be blank or contains less than 10 didgits");
        }
        Proovedores proovedortoEntity=mapper.toEntity(proovedor);
        Proovedores proovedoreSaved=repository.save(proovedortoEntity);
        return proovedoreSaved;
    }

    public List<Proovedores> findAll(){
         return repository.findAll();
    }

    public Proovedores findById(Integer id){
        return repository.findById(id)
                .orElseThrow(()->new ProveedorNotFoundException("user not founf by id:"+id));

    }

    public Proovedores updateProovedor(Proovedores proovedor){
        Proovedores provedorFound=repository.findById(proovedor.getId())
                .orElseThrow(()-> new ProveedorNotFoundException("producto not found with id:"+proovedor.getId()));

        if (!(proovedor.getNombre() ==null)&& !(proovedor.getNombre().isBlank())){
            provedorFound.setNombre(proovedor.getNombre());
        }
        if (!(proovedor.getTelefono()==null) && !(proovedor.getTelefono().isBlank())){
            if (proovedor.getTelefono().length()<10){
                throw new IllegalArgumentException("phone number can not be blank or contains less than 10 didgits");
            }
            provedorFound.setTelefono(proovedor.getTelefono());
        }
        if (!(proovedor.getCaracteristicas()==null)&& !(proovedor.getCaracteristicas().isBlank())){
            provedorFound.setCaracteristicas(proovedor.getCaracteristicas());
        }
        return repository.save(provedorFound);
    }
}
