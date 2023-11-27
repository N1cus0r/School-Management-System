import AuthenticatedApiClient from "../../services/authenticated-api-client.ts";
import {Course} from "../../entities/Course.ts";
import {useQuery} from "react-query";

const authenticatedApiClient = new AuthenticatedApiClient<Course>("/courses")
const useCourseData = (courseId: string) => useQuery({
    queryKey: ["courses", courseId],
    queryFn: () =>
        authenticatedApiClient
            .get(courseId),
});

export default useCourseData
