package pe.edu.colegiocima.appcima.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import pe.edu.colegiocima.appcima.exception.EntityNotFoundException;
import pe.edu.colegiocima.appcima.models.entity.AnioLectivo;
import pe.edu.colegiocima.appcima.models.entity.AreaAsignatura;
import pe.edu.colegiocima.appcima.models.entity.Grado;
import pe.edu.colegiocima.appcima.models.entity.PlanEstudio;
import pe.edu.colegiocima.appcima.service.PlanEstudioService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController(value = "planestudio")
@RequestMapping("/planestudio")
@Api(tags = "API Plan Estudio", description="API del Plan de Estudio")
public class PlanEstudioController {
    @Autowired
    private PlanEstudioService planEstudioService;

    @GetMapping()
    @ApiOperation(value = "Listar los planes de estudio")
    public ResponseEntity<?> listar(){
        return ResponseEntity.ok(planEstudioService.findAll());
    }

    @GetMapping("/listar")
    @ApiOperation(value = "Listar Plan de Estudio con Descripciones de Anio Lectivo, Grado, Area y Asignatura")
    public  ResponseEntity<?> listarCustom(){
        return ResponseEntity.ok(planEstudioService.findCustom());
    }

//    @GetMapping("/{id}")
//    @ApiOperation(value = "Obtener un plan de estudio según su ID")
//    public ResponseEntity<?> listarPorId(@ApiParam(value = "Identificador del Plan de Estudio") @PathVariable Short id){
//        return ResponseEntity.ok(planEstudioService.findById(id));
//    }

    @GetMapping("/aniolectivo/{idAnioLectivo}/grado/{idGrado}")
    @ApiOperation(value = "Obtener los Planes de Estudio de la vista")
    public ResponseEntity<?> listarVista(@ApiParam(value = "Identificador del Anio Lectivo") @PathVariable Short idAnioLectivo,
                                         @ApiParam(value = "Identificador del Grado") @PathVariable Short idGrado){
        return ResponseEntity.ok(planEstudioService.busquedaPersonalizada(idAnioLectivo,idGrado));
    }

    @GetMapping("/aniolectivo/{idAnioLectivo}/grado/{idGrado}/pagina")
    @ApiOperation(value = "Obtener los Planes de Estudio de la vista")
    public ResponseEntity<?> listarVista(@ApiParam(value = "Identificador del Anio Lectivo") @PathVariable Short idAnioLectivo,
                                         @ApiParam(value = "Identificador del Grado") @PathVariable Short idGrado, Pageable pageable){
        return ResponseEntity.ok(planEstudioService.busquedaPlanEstudio(idAnioLectivo,idGrado,pageable));
    }

    @PostMapping()
    @ApiOperation(value = "Crear un nuevo plan de estudio")
    public ResponseEntity<?> crear(@Valid @RequestBody @ApiParam(value = "Estructura del modelo Plan de Estudio") PlanEstudio planEstudio, BindingResult result){
        if(result.hasErrors()) {
            return this.validar(result);
        }
        PlanEstudio planEstudioDB = planEstudioService.save(planEstudio);
        return ResponseEntity.status(HttpStatus.CREATED).body(planEstudioDB);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Actualizar plan de estudio")
    public ResponseEntity<?> actualizar(@Valid @RequestBody @ApiParam(value = "Estructura del modelo Plan de Estudio") PlanEstudio planEstudio, BindingResult result,
                                        @ApiParam(value = "Identificador del Plan de Estudio") @PathVariable Short id){
        if(result.hasErrors()) {
            return this.validar(result);
        }

        PlanEstudio pE = planEstudioService.findById(id).orElse(null);

        if(Objects.isNull(pE)){
            return ResponseEntity.notFound().build();
        }

        AnioLectivo oAnioLectivo = Objects.nonNull(planEstudio.getAnioLectivo())? planEstudio.getAnioLectivo():new AnioLectivo();
        Grado oGrado = Objects.nonNull(planEstudio.getGrado())? planEstudio.getGrado():new Grado();
        AreaAsignatura oAreaAsignatura = Objects.nonNull(planEstudio.getAreaAsignatura())? planEstudio.getAreaAsignatura():new AreaAsignatura();

        PlanEstudio planEstudioDB = pE;
        planEstudioDB.setAnioLectivo(oAnioLectivo);
        planEstudioDB.setAreaAsignatura(oAreaAsignatura);
        planEstudioDB.setGrado(oGrado);
        planEstudioDB.setHoras(planEstudio.getHoras());

        return ResponseEntity.status(HttpStatus.CREATED).body(planEstudioService.save(planEstudioDB));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Eliminar un plan de estudio por su ID")
    public ResponseEntity<?> eliminar(@ApiParam(value = "Identificador del Plan de Estudio") @PathVariable Short id){
        planEstudioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Ver plan de estudio")
    public ResponseEntity<?> ver(
            @PathVariable
            @ApiParam(value = "Identificador del plan estudio",required = true,example = "16")
                    Short id) {
        PlanEstudio oPlanEstudio = planEstudioService.findById(id).orElseThrow(() -> new EntityNotFoundException(PlanEstudio.class,"id",id.toString()));
        return ResponseEntity.ok(oPlanEstudio);
    }

    private ResponseEntity<?> validar(BindingResult result){
        Map<String,Object> errores = new HashMap<>();
        List<FieldError> lError = result.getFieldErrors();
        for(FieldError error: lError){
            errores.put(error.getField(),"El campo " + error.getField() + " " + error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errores);
    }
}
