/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.quartz.application;

import java.util.Date;

import org.apache.commons.lang.Validate;

import com.globant.katari.quartz.domain.ScheduledCommand;

/** Simple DTO that holds information related to a quartz job for a scheduled
 * command.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class Task {

  /** The quartz schedule group name, never null.
   *
   * The group name and the job name uniquely identify a job.
   */
  private final String groupName;

  /** The quartz schedule name, never null.
   *
   * The group name and the job name uniquely identify a job.
   */
  private final String jobName;

  /** The command to run in this task, never null.
   */
  private final ScheduledCommand command;

  /** True if the task is currently running.
   */
  private final boolean isRunning;

  /** The time that the task will run again.
   *
   * This may be null if the task will never run again.
   */
  private final Date nextExecutionTime;

  /** Last time that the task ran, null if it never run before.
   */
  private final Date lastExecutionTime;

  /** Builds a new instance of the Task.
   *
   * @param theGroupName the name of the quartz group of this task. It cannot
   * be null.
   *
   * @param theJobName the name of the quartz job. It cannot be null.
   *
   * @param theCommand the command that the task runs. It cannot be null.
   *
   * @param running a boolean indicating if the job is currently running.
   *
   * @param theNextExecutionTime the time when the task will run. Null if it
   * will never run again.
   *
   * @param theLastExecutionTime the last time that the task ran, or null if it
   * never run before.
   */
  public Task(final String theGroupName, final String theJobName,
      final ScheduledCommand theCommand, final boolean running,
      final Date theNextExecutionTime, final Date theLastExecutionTime) {
    Validate.notNull(theGroupName, "The group name cannot be null");
    Validate.notNull(theJobName, "The job name cannot be null");
    Validate.notNull(theCommand, "The ScheduledCommand cannot be null");
    groupName = theGroupName;
    jobName = theJobName;
    command = theCommand;
    isRunning = running;
    nextExecutionTime = theNextExecutionTime;
    lastExecutionTime = theLastExecutionTime;
  }

  /** The group name.
   *
   * The group name and the job name uniquely identify a job in a quartz
   * scheduler.
   *
   * @return the quartz group name, never returns null.
   */
  public String getGroupName() {
    return groupName;
  }

  /** The job name.
   *
   * The group name and the job name uniquely identify a job in a quartz
   * scheduler.
   *
   * @return the quartz job name, never returns null.
   */
  public String getJobName() {
    return jobName;
  }

  /** The command to execute when the task runs.
   *
   * @return the command. Never returns null.
   */
  public ScheduledCommand getCommand() {
    return command;
  }

  /** Is the task running?
   *
   * @return a boolean indicating if the task is running.
   */
  public boolean isRunning() {
    return isRunning;
  }

  /** The time that the tasks will run.
   *
   * @return the time that the task will run, or null if it will never run
   * again, usually if it was a one time task.
   */
  public Date getNextExecutionTime() {
    return nextExecutionTime;
  }

  /** When the task ran for the last time.
   *
   * @return the time that the task last ran. Null if the it never ran.
   */
  public Date getLastExecutionTime() {
    return lastExecutionTime;
  }
}

