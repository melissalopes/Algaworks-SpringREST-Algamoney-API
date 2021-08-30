package com.algamoneyapi.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algamoneyapi.event.RecursoCriadoEvent;
import com.algamoneyapi.model.Categoria;
import com.algamoneyapi.repository.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	public List<Categoria> listar(){
		return categoriaRepository.findAll();
	}
	
//	OUTRA FORMA DE FAZER O MÉTODO ACIMA
//	@GetMapping
//	public ResponseEntity<?> listar(){
	
//		List<Categoria> categorias = categoriaRepository.findAll();
//		return categorias.isEmpty() ? ResponseEntity.ok(categorias) 
//				: ResponseEntity.noContent().build();
	
//** RETORNA A LISTA SE TIVER OBJETOS, MAS SE ESTIVER VAZIA RETORNA STATUS 204 "SEM CONTÉUDO".
//	}
	
	@PostMapping
	public ResponseEntity<Categoria> criar(@Validated @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
//	OUTRA FORMA DE FAZER O MÉTODO ACIMA SEM O 'EVENTS'
//	@PostMapping
//	public ResponseEntity<Categoria> criar(@Validated @RequestBody Categoria categoria, HttpServletResponse response) {
//		Categoria categoriaSalva = categoriaRepository.save(categoria);
//		
//		URI uri = ServletUriComponentsBuilder                ** Através dessa Classe Construtora 
//				.fromCurrentRequestUri()                     ** pega a URI da Requisição Atual
//				.path("/{codigo}")                           ** adiciona o código
//			    .buildAndExpand(categoriaSalva.getCodigo())  ** -> esse é o código 
//			    .toUri();                                    ** que vai ser adicionado aqui na URI
//		response.setHeader("Location", uri.toASCIIString()); ** E vai settar o header location com essa URI
//		                                                     
//		
//** Ou seja, no Header Location do Postman vai aparecer, por exemplo, esse endereço 'http://localhost:8080/categorias/7' **
//		
//		return ResponseEntity.created(uri).body(categoriaSalva);
//	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Optional<Categoria>> buscarPeloCodigo(@PathVariable Long codigo) {
		Optional<Categoria> categoria = categoriaRepository.findById(codigo);
		return  categoria.isPresent() ? ResponseEntity.ok(categoria) 
				: ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long codigo) {
		categoriaRepository.deleteById(codigo);
	}
	
}
