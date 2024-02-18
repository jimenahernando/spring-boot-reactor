package com.jimenahernando.springbootreactor;

import com.jimenahernando.springbootreactor.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
//        primerEjemplo();
//        primerEjemploReducido();
//        ejemploConTareaEnSuscribe();
//        ejemploConTareaEmulandoError();
//        ejemploConTareaFinalizada();
//        ejemploConMap();
//        ejemploConMapCambiandoOrden();
//        ejemploConMapConvertirStringAUsuario();
//        ejemploConFilter();
//        ejemploInmutabilidad();
//        ejemploCreoObservableApartirIterable();
//        ejemploFlatMap();
//        ejemploConvertirUsuarioAString();
        ejemploConvertirAMono();
    }

    private void primerEjemplo() {
        // creamos nuestro primer observable: un publisher
        Flux<String> nombres = Flux.just("Cecilia", "Camila", "Luis", "Graciela", "Jimena")
                .doOnNext(elemento -> System.out.println(elemento));//metodo evento donde ponemos la tarea

        // Alguien tiene que suscribirse (consumir) al observable para que pase algo
        // Puede consumirlo y hacer alguna tarea
        nombres.subscribe();
    }

    private void primerEjemploReducido() {
        //transformamos la funcion lambda, flecha o anonima a una REFERENCIA DE METODO
        Flux<String> nombres = Flux.just("Cecilia", "Camila", "Luis", "Graciela", "Jimena")
                .doOnNext(System.out::println);

        nombres.subscribe();
    }

    private void ejemploConTareaEnSuscribe() {
        Flux<String> nombres = Flux.just("Cecilia", "Camila", "Luis", "Graciela", "Jimena")
                .doOnNext(System.out::println);

        // nombres.subscribe(e -> log.info(e)); con lambda
        nombres.subscribe(log::info);
        //el doOnNext te permite ejecutar una tarea, lo mismo lo podemos realizar entre los parentesis del suscribe
        // vemos que primero se escribe en consola y luego en el log
    }

    private void ejemploConTareaEmulandoError() throws RuntimeException {
        Flux<String> nombres = Flux.just("Cecilia", "Camila", "", "Graciela", "Jimena")
                .doOnNext(elemento -> {
                    if (elemento.isEmpty()) {
                        throw new RuntimeException("Nombres no pueden ser vacios");
                    }
                    System.out.println(elemento);

                });

        nombres.subscribe(log::info,
                error -> log.error(error.getMessage()));
    }

    private void ejemploConTareaFinalizada() throws RuntimeException {
        //onComplete es cuando termina de emitir el flujo completo
        Flux<String> nombres = Flux.just("Cecilia", "Camila", "Luis", "Graciela", "Jimena")
                .doOnNext(elemento -> {
                    if (elemento.isEmpty()) {
                        throw new RuntimeException("Nombres no pueden ser vacios");
                    }
                    System.out.println(elemento);

                });

        nombres.subscribe(log::info,
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Ha finalizado al ejecución del observable con éxito!");
                    }
                });
    }

    private void ejemploConMap() throws RuntimeException {
        // el map no modifica el flujo original sino que retorna una
        // nueva instancia con las modificaciones
        //1ero imprimira en minuscula al emitir
        //2do imprimira en mayuscula ya que al recibir los recibe ya en mayuscula
        Flux<String> nombres = Flux.just("Cecilia", "Camila", "Luis", "Graciela", "Jimena")
                .doOnNext(elemento -> {
                    if (elemento.isEmpty()) {
                        throw new RuntimeException("Nombres no pueden ser vacios");
                    }
                    System.out.println(elemento);

                })
                .map(nombre -> nombre.toUpperCase());

        nombres.subscribe(log::info,
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Ha finalizado al ejecución del observable con éxito!");
                    }
                });
    }

    private void ejemploConMapCambiandoOrden() throws RuntimeException {
        // el map no modifica el flujo original sino que retorna una
        // nueva instancia con las modificaciones
        // Imprimira en ambos casos en mayuscula
        Flux<String> nombres = Flux.just("Cecilia", "Camila", "Luis", "Graciela", "Jimena")
                .map(nombre -> nombre.toUpperCase())
                .doOnNext(elemento -> {
                    if (elemento.isEmpty()) {
                        throw new RuntimeException("Nombres no pueden ser vacios");
                    }
                    System.out.println(elemento);

                });

        nombres.subscribe(log::info,
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Ha finalizado al ejecución del observable con éxito!");
                    }
                });
    }

    private void ejemploConMapConvertirStringAUsuario() throws RuntimeException {
        // el map no modifica el flujo original sino que retorna una
        // nueva instancia con las modificaciones
        // Imprimira en ambos casos en mayuscula
        Flux<Usuario> nombres = Flux.just("Cecilia", "Camila", "Luis", "Graciela", "Jimena")
                .map(nombre -> new Usuario(nombre.toUpperCase(), null))
                .doOnNext(usuario -> {
                    if (usuario == null) {
                        throw new RuntimeException("Nombres no pueden ser vacios");
                    }
                    System.out.println(usuario.getNombre());
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                });

        // implementamos el metodo toString() para que no imprima asi
        // com.jimenahernando.springbootreactor.models.Usuario@13cda7c9
        nombres.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Ha finalizado al ejecución del observable con éxito!");
                    }
                });
    }

    private void ejemploConFilter() throws RuntimeException {
        Flux<Usuario> nombres = Flux.just("Cecilia Hernando", "Camila Riera", "Luis Riera", "Graciela Barreira", "Jimena Hernando")
                .map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
                //adentro del filter se evalua una funcion booleana
                .filter(usuario -> usuario.getApellido().equalsIgnoreCase("Hernando"))
                .doOnNext(usuario -> {
                    if (usuario == null) {
                        throw new RuntimeException("Nombres no pueden ser vacios");
                    }
                    System.out.println(usuario.getNombre());
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                });

        // implementamos el metodo toString() para que no imprima asi
        // com.jimenahernando.springbootreactor.models.Usuario@13cda7c9
        nombres.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Ha finalizado al ejecución del observable con éxito!");
                    }
                });
    }

    private void ejemploInmutabilidad() throws RuntimeException {
        Flux<String> nombres = Flux.just("Cecilia Hernando", "Camila Riera", "Luis Riera", "Graciela Barreira", "Jimena Hernando");

        Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
                .filter(usuario -> usuario.getApellido().equalsIgnoreCase("Hernando"))
                .doOnNext(usuario -> {
                    if (usuario == null) {
                        throw new RuntimeException("Nombres no pueden ser vacios");
                    }
                    System.out.println(usuario.getNombre());
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                });

        // implementamos el metodo toString() para que no imprima asi
        // com.jimenahernando.springbootreactor.models.Usuario@13cda7c9
        usuarios.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Ha finalizado al ejecución del observable con éxito!");
                    }
                });
    }

    private void ejemploCreoObservableApartirIterable() {
        List<String> nombresList = new ArrayList<>();
        nombresList.add("Cecilia Hernando");
        nombresList.add("Camila Riera");
        nombresList.add("Luis Riera");
        nombresList.add("Graciela Barreira");
        nombresList.add("Jimena Hernando");

        Flux<String> nombres = Flux.fromIterable(nombresList);

        Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
                .filter(usuario -> usuario.getApellido().equalsIgnoreCase("Riera"))
                .doOnNext(usuario -> {
                    if (usuario == null) {
                        throw new RuntimeException("Nombres no pueden ser vacios");
                    }
                    System.out.println(usuario.getNombre());
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                });

        usuarios.subscribe(e -> log.info(e.toString()),
                error -> log.error(error.getMessage()),
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Ha finalizado al ejecución del observable con éxito!");
                    }
                });
    }

    private void ejemploFlatMap() {
        List<String> nombresList = new ArrayList<>();
        nombresList.add("Cecilia Hernando");
        nombresList.add("Camila Riera");
        nombresList.add("Luis Riera");
        nombresList.add("Graciela Barreira");
        nombresList.add("Jimena Hernando");

        Flux.fromIterable(nombresList)
                .map(nombre -> new Usuario(nombre.split(" ")[0], nombre.split(" ")[1]))
                .flatMap(usuario -> {
                    if(usuario.getApellido().equalsIgnoreCase("Riera")){
                        // tiene que devolver un observable, a partir del objeto creamos un observable y lo devolvemos
                        //  fusionandolo al unico string de salida (lo aplana).
                        return Mono.just(usuario);
                    } else {
                        return Mono.empty();
                    }
                })
                .map(usuario -> {
                    String nombre = usuario.getNombre().toLowerCase();
                    usuario.setNombre(nombre);
                    return usuario;
                })
                .subscribe(u -> log.info(u.toString()));
    }

    private void ejemploConvertirUsuarioAString() {
        List<Usuario> usuariosList = new ArrayList<>();
        usuariosList.add(new Usuario("Cecilia","Hernando"));
        usuariosList.add(new Usuario("Camila","Riera"));
        usuariosList.add(new Usuario("Luis","Riera"));
        usuariosList.add(new Usuario("Graciela","Barreira"));
        usuariosList.add(new Usuario("Jimena","Hernando"));

        Flux.fromIterable(usuariosList)
                .map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
                        .flatMap(nombre -> {
                            if(nombre.contains("Jimena".toUpperCase())){
                                return Mono.just(nombre);
                            } else {
                                return Mono.empty();
                            }
                        })
                .map(String::toLowerCase)
                .subscribe(log::info);
    }

    private void ejemploConvertirAMono() {
        // Mono o collectList
        List<Usuario> usuariosList = new ArrayList<>();
        usuariosList.add(new Usuario("Cecilia","Hernando"));
        usuariosList.add(new Usuario("Camila","Riera"));
        usuariosList.add(new Usuario("Luis","Riera"));
        usuariosList.add(new Usuario("Graciela","Barreira"));
        usuariosList.add(new Usuario("Jimena","Hernando"));

        // va recibiendo uno a uno
        Flux.fromIterable(usuariosList)
                .subscribe(usuario -> log.info(usuario.toString()));

        // recibe un solo flujo con todos los usuarios
        Flux.fromIterable(usuariosList)
                // convierte a un flujo
                .collectList()
                .subscribe(lista -> log.info(lista.toString()));

        // recibe un solo flujo pero despues lo recorre elemento por elemento
        Flux.fromIterable(usuariosList)
                // convierte a un flujo
                .collectList()
                .subscribe(lista -> lista.forEach(item -> log.info(item.toString())));
    }
}
