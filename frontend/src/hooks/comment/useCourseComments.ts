import useCourseEntities from "../course/useCourseEntities.ts";
import {Comment} from "../../entities/Comment.ts";

const useCourseComments = (courseId: number | string) =>
  useCourseEntities<Comment>(courseId, "comments");

export default useCourseComments

