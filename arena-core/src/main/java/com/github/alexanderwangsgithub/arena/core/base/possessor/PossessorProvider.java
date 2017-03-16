package com.github.alexanderwangsgithub.arena.core.base.possessor;

/**
 * arena
 *
 * @author Alexander Wang
 * @bio https://alexanderwangsgithub.github.io/
 * @email alexanderwangwork@outlook.com
 * @date 09/02/2017
 */
public interface PossessorProvider {
    <A extends Possessor> A getPossessor(Class<A> possessorType);
    <A extends Possessor> A getWrappedActor(Class<A> wrappedActorType);


}
