import { Link, Outlet } from 'react-router-dom';

export function RouteLayout() {
  return (
    <div className="route-layout">
      <nav className="route-nav">
        <Link to="/">Home</Link>
        <Link to="/dashboard">Dashboard</Link>
      </nav>
      <section className="route-content">
        <Outlet />
      </section>
    </div>
  );
}
