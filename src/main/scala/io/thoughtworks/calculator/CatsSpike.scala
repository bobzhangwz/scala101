package io.thoughtworks.calculator

import cats.data.{EitherK, State, StateT}
import cats.free.{Free, FreeT}
import cats.free.Free.liftF
import cats.{Eval, Id, InjectK, ~>}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.StdIn.readLine

object CatsSpike extends App {
  sealed trait KVStoreA[A]
  case class Put[T](key: String, value: T) extends KVStoreA[Unit]
  case class Get[T](key: String) extends KVStoreA[Option[T]]
  case class Delete(key: String) extends KVStoreA[Unit]

  type KVStore[A] = Free[KVStoreA, A]

  // Put returns nothing (i.e. Unit).
  def put[T](key: String, value: T): KVStore[Unit] =
    liftF[KVStoreA, Unit](Put[T](key, value))

  // Get returns a T value.
  def get[T](key: String): KVStore[Option[T]] =
    liftF[KVStoreA, Option[T]](Get[T](key))

  // Delete returns nothing (i.e. Unit).
  def delete(key: String): KVStore[Unit] =
    liftF(Delete(key))

  // Update composes get and set, and returns nothing.
  def update[T](key: String, f: T => T): KVStore[Unit] =
    for {
      vMaybe <- get[T](key)
      _ <- vMaybe.map(v => put[T](key, f(v))).getOrElse(Free.pure(()))
    } yield ()

  def program: KVStore[Option[Int]] =
    for {
      _ <- put("wild-cats", 2)
      _ <- update[Int]("wild-cats", (_ + 12))
      _ <- put("tame-cats", 5)
      n <- get[Int]("wild-cats")
      _ <- delete("tame-cats")
    } yield n

  def impureCompiler: KVStoreA ~> Id = new (KVStoreA ~> Id) {
    val kvs = mutable.Map.empty[String, Any]
    override def apply[A](fa: KVStoreA[A]): Id[A] =
      fa match {
        case Put(key, value) =>
          println(s"put($key, $value)")
          kvs(key) = value
          ()
        case Get(key) =>
          println(s"get($key)")
          kvs.get(key).map(_.asInstanceOf[A])
        case Delete(key) =>
          println(s"delete($key)")
          kvs.remove(key)
          ()
      }
  }

  program.foldMap(impureCompiler)
}

object CatsSpike2 extends App {
  /* Handles user interaction */
  sealed trait Interact[A]
  case class Ask(prompt: String) extends Interact[String]
  case class Tell(msg: String) extends Interact[Unit]

  /* Represents persistence operations */
  sealed trait DataOp[A]
  case class AddCat(a: String) extends DataOp[Unit]
  case class GetAllCats() extends DataOp[List[String]]

  type CatsApp[A] = EitherK[DataOp, Interact, A]

  class Interacts[F[_]](implicit I: InjectK[Interact, F]) {
    def tell(msg: String): Free[F, Unit] = Free.inject[Interact, F](Tell(msg))
    def ask(prompt: String): Free[F, String] = Free.inject[Interact, F](Ask(prompt))
  }

  object Interacts {
    implicit def interacts[F[_]](implicit I: InjectK[Interact, F]): Interacts[F] = new Interacts[F]
  }

  class DataSource[F[_]](implicit I: InjectK[DataOp, F]) {
    def addCat(a: String): Free[F, Unit] = Free.inject[DataOp, F](AddCat(a))
    def getAllCats: Free[F, List[String]] = Free.inject[DataOp, F](GetAllCats())
  }

  object DataSource {
    implicit def dataSource[F[_]](implicit I: InjectK[DataOp, F]): DataSource[F] = new DataSource[F]
  }

  object ConsoleCatsInterpreter extends (Interact ~> Id) {
    def apply[A](i: Interact[A]) = i match {
      case Ask(prompt) =>
        println(prompt)
        readLine()
      case Tell(msg) =>
        println(msg)
    }
  }

  object InMemoryDatasourceInterpreter extends (DataOp ~> Id) {

    private[this] val memDataSet = new ListBuffer[String]

    def apply[A](fa: DataOp[A]) = fa match {
      case AddCat(a) => memDataSet.append(a); ()
      case GetAllCats() => memDataSet.toList
    }
  }

  val interpreter: CatsApp ~> Id = InMemoryDatasourceInterpreter or ConsoleCatsInterpreter

}

object FreeTSpike extends App {
  /* A base ADT for the user interaction without state semantics */
  sealed abstract class Teletype[A] extends Product with Serializable
  // defined class Teletype

  final case class WriteLine(line : String) extends Teletype[Unit]
  // defined class WriteLine

  final case class ReadLine(prompt : String) extends Teletype[String]
  // defined class ReadLine

  type TeletypeT[M[_], A] = FreeT[Teletype, M, A]
  // defined type alias TeletypeT

  type Log = List[String]
  // defined type alias Log

  type TeletypeState[A] = State[List[String], A]
  // defined type alias TeletypeState

  object TeletypeOps {
    def writeLine(line : String) : TeletypeT[TeletypeState, Unit] =
      FreeT.liftF[Teletype, TeletypeState, Unit](WriteLine(line))
    def readLine(prompt : String) : TeletypeT[TeletypeState, String] =
      FreeT.liftF[Teletype, TeletypeState, String](ReadLine(prompt))
    def log(s : String) : TeletypeT[TeletypeState, Unit] =
      FreeT.liftT[Teletype, TeletypeState, Unit](State.modify(s :: _))
  }

  def program : TeletypeT[TeletypeState, Unit] = {
    for {
      userSaid <- TeletypeOps.readLine("what's up?!")
      _ <- TeletypeOps.log(s"user said : $userSaid")
      _ <- TeletypeOps.writeLine("thanks, see you soon!")
    } yield ()
  }

  def interpreter = new (Teletype ~> TeletypeState) {
    def apply[A](fa: Teletype[A]): TeletypeState[A] = {
      fa match {
        case ReadLine(prompt) =>
          println(prompt)
          val userInput = "hanging in here" //scala.io.StdIn.readLine()
          StateT.pure[Eval, List[String], A](userInput)
        case WriteLine(line) =>
          StateT.pure[Eval, List[String], A](println(line))
      }
    }
  }
}
