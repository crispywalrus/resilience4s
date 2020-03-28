package sttp.resilience4s.cats

import sttp.resilience4s.monad.MonadError

class CatsMonadError[F[_]](implicit F: cats.MonadError[F, Throwable]) extends MonadError[F] {
  override def unit[T](t: T): F[T] = F.pure(t)

  override def map[T, T2](fa: F[T])(f: T => T2): F[T2] = F.map(fa)(f)

  override def flatMap[T, T2](fa: F[T])(f: T => F[T2]): F[T2] =
    F.flatMap(fa)(f)

  override def raiseError[T](t: Throwable): F[T] = F.raiseError(t)

  override protected def handleWrappedError[T](rt: F[T])(h: PartialFunction[Throwable, F[T]]): F[T] =
    F.recoverWith(rt)(h)
}
