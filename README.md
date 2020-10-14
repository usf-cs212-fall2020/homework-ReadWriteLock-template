Read Write Lock
=================================================

For this homework assignment, you will create (1) a custom read write lock and (2) a thread safe indexed set using that lock. The custom read write lock must be able to detect incorrect `unlock()` calls to its write lock as well.

See the Javadoc comments in the template code for additional details.

## Hints ##

Below are some hints that may help with this homework assignment:

  - Look at the lecture notes for discussion on the custom read write lock.

  - Decide whether a block of operations are read operations, write operations, or mixed (and hence need a write lock) **with respect to the shared data**.

  - The provided tests are not fool-proof. They try to recreate conditions that will cause issues if your lock or set are incorrectly implemented, but just because the tests pass does not mean your implementations are correct. Run the tests several times!

You are not required to use these hints in your solution. There may be multiple approaches to solving this homework.

## Requirements ##

See the Javadoc and `TODO` comments in the template code in the `src/main/java` directory for additional details. You must pass the tests provided in the `src/test/java` directory. Do not modify any of the files in the `src/test` directory.

See the [Homework Guides](https://usf-cs212-fall2020.github.io/guides/homework/) for additional details on homework requirements and submission.
