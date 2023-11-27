import useHomeworks from "../hooks/homework/useHomeworks.ts";
import ErrorMessage from "../components/shared/ErrorMessage.tsx";
import LoadingSpinner from "../components/shared/LoadingSpinner.tsx";
import ContentPlaceholder from "../components/shared/ContentPlaceholder.tsx";
import {Spinner,} from "@chakra-ui/react";
import PageUtils from "../utils/page-utils.ts";
import InfiniteScroll from "react-infinite-scroll-component";
import HomeworksTable from "../components/homework/HomeworksTable.tsx";

const HomeworksPage = () => {
  const {
    data: homeworks,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
  } = useHomeworks();

  if (isLoading) return <LoadingSpinner />;

  if (!homeworks || error) return <ErrorMessage />;

  if (homeworks.pages[0].length === 0) return <ContentPlaceholder />;

  const fetchedDataCount = PageUtils.getFetchedDataCount(homeworks);

  return (
    <>
      <InfiniteScroll
        next={() => fetchNextPage()}
        hasMore={!!hasNextPage}
        loader={<Spinner />}
        dataLength={fetchedDataCount}
      >
        <HomeworksTable homeworkPages={homeworks.pages}/>
      </InfiniteScroll>
    </>
  );
};
export default HomeworksPage;
