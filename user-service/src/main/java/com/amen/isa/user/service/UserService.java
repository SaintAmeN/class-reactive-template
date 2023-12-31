package com.amen.isa.user.service;

import com.amen.isa.component.repository.UserRepository;
import com.amen.isa.component.sse.Emitter;
import com.amen.isa.model.domain.StoreUser;
import com.amen.isa.model.mapper.UserMapper;
import com.amen.isa.model.request.AddUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final Emitter<StoreUser> emitter = new Emitter<>();

    private final UserRepository userRepository;
    private final UserMapper userMapper;
//    private final TailableCursorRequest tailableCursorRequest;
//    private final ReactiveMongoTemplate reactiveMongoTemplate;
//    private final MongoTemplate mongoTemplate;
//    private final ReactiveMongoOperations mongoOperations;

    public Flux<StoreUser> getAll() {
        return userRepository.findAll().log();
    }

//    TODO: (optional) add
//    TODO: (optional) add replica and subscribe to collection
//    public Flux<StoreUser> subscribe() {
//        return mongoTemplate
//                .changeStream(StoreUser.class)
//                .watchCollection("store.user")
//                .listen()
//                .map(ChangeStreamEvent::getBody);
//    }


    public Mono<StoreUser> addUser(final AddUserRequest request) {
        var user = userMapper.addUserRequestToStoreUser(request);
        return userRepository.save(user)
                .map(storeUser -> {
                    emitter.emit(storeUser);
                    return storeUser;
                });
    }

    public Emitter<StoreUser> getEmitter() {
        return emitter;
    }

    public Flux<String> tailUsers() {
        return userRepository.findAllByFirstNameContaining("*");
    }
}
