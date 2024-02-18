package com.jimenahernando.springbootreactor;

import com.jimenahernando.springbootreactor.models.Comentarios;
import com.jimenahernando.springbootreactor.models.Usuario;
import com.jimenahernando.springbootreactor.models.UsuarioComentarios;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
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
//        ejemploConvertirAMono();
//        ejemploCombinarFlujos();
//        ejemploCombinarConZipWith();
//        ejemploCombinarConZipWith2();
//        ejemploZipWithyRange();
//        ejemploZipWithEInterval();
//        ejemploDelayElements();
//        ejemploIntervaloInfinito();
//        ejemploCrearObservableFlux();
//        ejemploContrapresion();
        ejemploContrapresionLimitRate();
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
                    if (usuario.getApellido().equalsIgnoreCase("Riera")) {
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
        usuariosList.add(new Usuario("Cecilia", "Hernando"));
        usuariosList.add(new Usuario("Camila", "Riera"));
        usuariosList.add(new Usuario("Luis", "Riera"));
        usuariosList.add(new Usuario("Graciela", "Barreira"));
        usuariosList.add(new Usuario("Jimena", "Hernando"));

        Flux.fromIterable(usuariosList)
                .map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
                .flatMap(nombre -> {
                    if (nombre.contains("Jimena".toUpperCase())) {
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
        usuariosList.add(new Usuario("Cecilia", "Hernando"));
        usuariosList.add(new Usuario("Camila", "Riera"));
        usuariosList.add(new Usuario("Luis", "Riera"));
        usuariosList.add(new Usuario("Graciela", "Barreira"));
        usuariosList.add(new Usuario("Jimena", "Hernando"));

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

    private void ejemploCombinarFlujos() {
        // 1era forma llamando a un metodo
        Mono<Usuario> usuarioMono = Mono.fromCallable(() -> crearUsuario());

        // 2da forma creandolo al vuelo
        Mono<Usuario> usuarioMonoAlVuelo = Mono.fromCallable(() -> new Usuario("Gisela", "Marozzi"));

        Mono<Comentarios> comentariosAlVuelo = Mono.fromCallable(() -> {
            Comentarios comentarios = new Comentarios();
            comentarios.addComentarios("Hola amiga que tal");
            comentarios.addComentarios("La casa esta preciosa");
            comentarios.addComentarios("Estoy tomando unos mates pensando en ti");
            return comentarios;
        });

        usuarioMonoAlVuelo.flatMap(u -> comentariosAlVuelo.map(c -> new UsuarioComentarios(u, c)))
                .subscribe(uc -> log.info(uc.toString()));
    }

    private Usuario crearUsuario() {
        return new Usuario("Gisela", "Marozzi");
    }

    private void ejemploCombinarConZipWith() {
        log.info("INIT ejemploCombinarConZipWith");
        Mono<Usuario> usuarioMonoAlVuelo = Mono.fromCallable(() -> new Usuario("Gisela", "Marozzi"));

        Mono<Comentarios> comentariosAlVuelo = Mono.fromCallable(() -> {
            Comentarios comentarios = new Comentarios();
            comentarios.addComentarios("Hola amiga que tal");
            comentarios.addComentarios("La casa esta preciosa");
            comentarios.addComentarios("Estoy tomando unos mates pensando en ti");
            return comentarios;
        });

        Mono<UsuarioComentarios> usuariosComentarios = usuarioMonoAlVuelo.zipWith(comentariosAlVuelo, (usuario, comentario) -> new UsuarioComentarios(usuario, comentario));
        usuariosComentarios.subscribe(uc -> log.info(uc.toString()));
    }

    private void ejemploCombinarConZipWith2() {
        // Otra forma de implementar ZipWith
        log.info("INIT ejemploCombinarConZipWith2");
        Mono<Usuario> usuarioMonoAlVuelo = Mono.fromCallable(() -> new Usuario("Gisela", "Marozzi"));

        Mono<Comentarios> comentariosAlVuelo = Mono.fromCallable(() -> {
            Comentarios comentarios = new Comentarios();
            comentarios.addComentarios("Hola amiga que tal");
            comentarios.addComentarios("La casa esta preciosa");
            comentarios.addComentarios("Estoy tomando unos mates pensando en ti");
            return comentarios;
        });

        Mono<UsuarioComentarios> usuariosComentarios = usuarioMonoAlVuelo
                .zipWith(comentariosAlVuelo)
                .map(tupla -> {
                    Usuario u = tupla.getT1();
                    Comentarios c = tupla.getT2();
                    return new UsuarioComentarios(u, c);
                });
        usuariosComentarios.subscribe(uc -> log.info(uc.toString()));
    }

    private void ejemploZipWithyRange() {
        Flux.just(1, 2, 3, 4)
                .map(e -> (e * 2))
                .zipWith(Flux.range(0, 4), (uno, dos) -> String.format("Primer flux: %d Segundo flux: %d", uno, dos))
                .subscribe(texto -> log.info(texto));

        Flux<Integer> rango = Flux.range(0, 4);
        Flux.just(1, 2, 3, 4)
                .map(e -> (e * 2))
                .zipWith(rango, (uno, dos) -> String.format("Primer flux: %d Segundo flux: %d", uno, dos))
                .subscribe(texto -> log.info(texto));
    }

    private void ejemploZipWithEInterval() {
        // no se visualiza porque sigue ejecutandose en otro hilo,
        // justamente la programacion reactiva no bloquea, sigue ejecutandose en segundo plano
/*        Flux<Integer> rango = Flux.range(1,12);
        Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));
        rango.zipWith(retraso, (ran, ret)-> ran)
                .doOnNext(i -> log.info(i.toString()))
                .subscribe();*/

        // para verlo, que se bloquee, auqnue no tiene ningun sentido
        // podemos hacer lo siguiente FORZAR EL BLOQUEO y se desbloquea cuando termina
        Flux<Integer> rango1 = Flux.range(1, 4);
        Flux<Long> retraso1 = Flux.interval(Duration.ofSeconds(1));
        rango1.zipWith(retraso1, (ran, ret) -> ran)
                .doOnNext(i -> log.info(i.toString()))
                .blockLast();
    }

    private void ejemploDelayElements() throws InterruptedException {
        //otra forma de aplicar intervalo de tiempos, mucho mas simple
        Flux<Integer> rango = Flux.range(1, 12)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(i -> log.info(i.toString()));

        rango.subscribe();

        // bloquea hasta que se haya emitido el ultimo elemento
        // no es recomendable ya que puede generar cuellos de botella
        // aca lo estamos utilizando para verlo por consola
        //rango.blockLast();

        // para hacer una pausa en milisegundos
        Thread.sleep(13000);
    }

    private void ejemploIntervaloInfinito() throws InterruptedException {
        // para verlo por consola tenemos que bloquear
        // vamos a bloquearlo de otra forma utilizando un contador
        // comienza en 1 y tienen que decrementar a 0 y cuando
        // llegue a 0 liberara el hilo
        CountDownLatch latch = new CountDownLatch(1);

        Flux.interval(Duration.ofSeconds(1))
                // para decrementar
                .doOnTerminate(() -> latch.countDown()) //latch::countDown
                .flatMap(i -> {
                    if (i >= 5) {
                        return Flux.error(new InterruptedException("Solo hasta 5"));
                    }
                    return Flux.just(i);
                })
                .map(i -> "Hola " + i)
                //este operador va a intentar ejecutarlo n veces antes de tirar el error
                .retry(2)
                .subscribe(s -> log.info(s), e -> log.error(e.getMessage()));

        //queda bloqueado hasta que llegue a cero el contador
        // como nunca se va a dar es infinito
        //para finalizarlo hay que detenerlo
        latch.await();
    }

    private void ejemploCrearObservableFlux() {
        Flux.create(emitter -> {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        private Integer contador = 0;

                        @Override
                        public void run() {
                            // con el emmiter cada elemento que queremos emitir lo hacemos con el next
                            emitter.next(++contador);
                            if (contador == 8){
                                timer.cancel();
                                emitter.complete();
                            }
                            if(contador == 5){
                                timer.cancel();
                                emitter.error(new InterruptedException("Error se ha detenido el flux en 5"));
                            }
                        }
                    }, 500, 1000);
                })
                .subscribe(next -> log.info(next.toString()),
                        error -> log.error(error.getMessage()),
                        // solo se ejecuta cuando se termina sin error
                        () -> log.info("Hemos terminado")
                );
    }

    private void ejemploContrapresion(){
        /*
         la contrapresion le permite al suscriptor avisarle al emisor
         la cantidad de elementos a enviar por cada vez.
         en vez de enviarle todo a la vez por ejemplo cada 5 elementos, en lotes
        */

        Flux.range(0,10)
                // nos permite ver la traza completa de nuestro flux
                // unbounded (ilimitada)
                .log()
                .subscribe(new Subscriber<Integer>() {

                    private Subscription s;
                    //para procesar por lotes de a 5 elementos
                    private Integer limite = 5;
                    private Integer consumido = 0;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.s = subscription;
                        s.request(limite);
                    }

                    @Override
                    public void onNext(Integer i) {
                        log.info(i.toString());
                        consumido++;
                        if(consumido == limite){
                            consumido = 0;
                            s.request(limite);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    private void ejemploContrapresionLimitRate(){
        Flux.range(0,10)
                .log()
                .limitRate(2)
                .subscribe();
    }
}
