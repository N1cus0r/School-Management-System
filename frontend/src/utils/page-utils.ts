export class PageUtils {
  static getFetchedDataCount = (data: { pages: any[][] }) => {
    return data.pages.reduce((total, page) => total + page.length, 0);
  };
}

export default PageUtils;
